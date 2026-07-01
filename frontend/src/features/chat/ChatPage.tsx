import { type FormEvent, type ReactNode, useEffect, useMemo, useRef, useState } from "react";
import axios from "axios";
import { getCurrentUser } from "../../lib/auth";
import {
    getChatMessages,
    getChatMentionUsers,
    getChatRooms,
    sendChatMessage,
    type ChatMention,
    type ChatMessage,
    type ChatRoom,
    type ChatRoomMember,
    type ChatTypingEvent,
} from "./chatApi";
import { ChatSocketClient, type ChatSocketStatus } from "./chatSocket";

type TypingUser = {
    username: string;
    lastSeenAt: number;
};

type MentionToken = {
    start: number;
    query: string;
};

type MentionMatch = {
    index: number;
    token: string;
    mention: ChatMention;
};

function formatMessageTime(value: string) {
    return new Intl.DateTimeFormat("en-IN", {
        hour: "2-digit",
        minute: "2-digit",
    }).format(new Date(value));
}

function getRoleLabel(role?: string) {
    return role ? role.replace("ROLE_", "").toLowerCase() : "user";
}

function getMemberLabel(member: ChatRoomMember) {
    return member.username || `User ${member.userId}`;
}

function getMentionToken(value: string): MentionToken | null {
    const start = value.lastIndexOf("@");

    if (start < 0) return null;

    const previous = start === 0 ? " " : value[start - 1];

    if (!/\s/.test(previous)) return null;

    const query = value.slice(start + 1);

    if (/\s/.test(query)) return null;

    return { start, query: query.toLowerCase() };
}

function findNextMention(text: string, mentions: ChatMention[], startAt: number): MentionMatch | null {
    const lowerText = text.toLowerCase();
    let nextMatch: MentionMatch | null = null;

    for (const mention of mentions) {
        if (!mention.username) continue;

        const token = `@${mention.username}`;
        const index = lowerText.indexOf(token.toLowerCase(), startAt);

        if (index < 0) continue;

        if (!nextMatch || index < nextMatch.index || (index === nextMatch.index && token.length > nextMatch.token.length)) {
            nextMatch = { index, token, mention };
        }
    }

    return nextMatch;
}

function renderMessageText(message: ChatMessage): ReactNode[] {
    const text = message.message;
    const mentions = [...(message.mentions ?? [])].sort((left, right) => (right.username?.length ?? 0) - (left.username?.length ?? 0));
    const nodes: ReactNode[] = [];
    let cursor = 0;

    while (cursor < text.length) {
        const match = findNextMention(text, mentions, cursor);

        if (!match) {
            nodes.push(text.slice(cursor));
            break;
        }

        if (match.index > cursor) {
            nodes.push(text.slice(cursor, match.index));
        }

        nodes.push(
            <span className="chat-mentioned-user" key={`${message.messageId}-${match.mention.userId}-${match.index}`}>
                {text.slice(match.index, match.index + match.token.length)}
            </span>,
        );

        cursor = match.index + match.token.length;
    }

    return nodes;
}

function getErrorMessage(error: unknown) {
    if (!axios.isAxiosError(error)) {
        return "Could not load chat.";
    }

    if (error.response?.status === 401 || error.response?.status === 403) {
        return "You are not allowed to use chat.";
    }

    if (!error.response) {
        return "Cannot reach chat-service. Check that it is running on port 8087.";
    }

    return `Chat failed: ${error.response.status}`;
}

export function ChatPage() {
    const currentUser = useMemo(() => getCurrentUser(), []);
    const [rooms, setRooms] = useState<ChatRoom[]>([]);
    const [activeRoomId, setActiveRoomId] = useState<number | null>(null);
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [roomMembers, setRoomMembers] = useState<ChatRoomMember[]>([]);
    const [selectedMentions, setSelectedMentions] = useState<ChatRoomMember[]>([]);
    const [draftMessage, setDraftMessage] = useState("");
    const [typingUsers, setTypingUsers] = useState<Map<number, TypingUser>>(new Map());
    const [socketStatus, setSocketStatus] = useState<ChatSocketStatus>("disconnected");
    const [error, setError] = useState("");
    const [isLoadingRooms, setIsLoadingRooms] = useState(true);
    const [isLoadingMessages, setIsLoadingMessages] = useState(false);
    const [isSendingMessage, setIsSendingMessage] = useState(false);
    const socketRef = useRef<ChatSocketClient | null>(null);
    const inputRef = useRef<HTMLInputElement | null>(null);
    const messagesEndRef = useRef<HTMLDivElement | null>(null);
    const typingStopTimerRef = useRef<number | null>(null);
    const isTypingRef = useRef(false);

    const activeRoom = useMemo(
        () => rooms.find((room) => room.roomId === activeRoomId) ?? null,
        [activeRoomId, rooms],
    );

    const typingLabel = useMemo(() => {
        const names = Array.from(typingUsers.values()).map((typingUser) => typingUser.username);

        if (names.length === 0) return "";
        if (names.length === 1) return `${names[0]} is typing`;
        if (names.length === 2) return `${names[0]} and ${names[1]} are typing`;

        return `${names[0]} and ${names.length - 1} others are typing`;
    }, [typingUsers]);

    const mentionToken = useMemo(() => getMentionToken(draftMessage), [draftMessage]);

    const mentionSuggestions = useMemo(() => {
        if (!mentionToken) return [];

        return roomMembers
            .filter((member) => member.userId !== currentUser?.userId)
            .filter((member) => !selectedMentions.some((selectedMention) => selectedMention.userId === member.userId))
            .filter((member) => getMemberLabel(member).toLowerCase().includes(mentionToken.query));
    }, [currentUser?.userId, mentionToken, roomMembers, selectedMentions]);

    useEffect(() => {
        let isMounted = true;

        async function loadRooms() {
            setIsLoadingRooms(true);
            setError("");

            try {
                const data = await getChatRooms();

                if (isMounted) {
                    setRooms(data);
                    setActiveRoomId(data[0]?.roomId ?? null);
                }
            } catch (err) {
                if (isMounted) {
                    setError(getErrorMessage(err));
                }
            } finally {
                if (isMounted) {
                    setIsLoadingRooms(false);
                }
            }
        }

        void loadRooms();

        return () => {
            isMounted = false;
        };
    }, []);

    useEffect(() => {
        if (!activeRoomId) return;

        let isMounted = true;
        const roomId = activeRoomId;

        async function loadMessages() {
            setIsLoadingMessages(true);
            setError("");
            setTypingUsers(new Map());

            try {
                const data = await getChatMessages(roomId);

                if (isMounted) {
                    setMessages(data);
                }
            } catch (err) {
                if (isMounted) {
                    setError(getErrorMessage(err));
                }
            } finally {
                if (isMounted) {
                    setIsLoadingMessages(false);
                }
            }
        }

        void loadMessages();

        return () => {
            isMounted = false;
        };
    }, [activeRoomId]);

    useEffect(() => {
        if (!activeRoomId) {
            setRoomMembers([]);
            return;
        }

        let isMounted = true;
        const roomId = activeRoomId;

        async function loadMembers() {
            try {
                const data = await getChatMentionUsers(roomId);

                if (isMounted) {
                    setRoomMembers(Array.isArray(data) ? data : []);
                }
            } catch (err) {
                if (isMounted) {
                    setError(getErrorMessage(err));
                }
            }
        }

        setSelectedMentions([]);
        void loadMembers();

        return () => {
            isMounted = false;
        };
    }, [activeRoomId]);

    useEffect(() => {
        const token = localStorage.getItem("token");

        if (!activeRoomId || !token || !currentUser) return;

        socketRef.current?.disconnect();

        const socketClient = new ChatSocketClient({
            token,
            roomId: activeRoomId,
            onStatusChange: setSocketStatus,
            onError: setError,
            onMessage: (message) => {
                setMessages((current) => {
                    if (current.some((existing) => existing.messageId === message.messageId)) return current;
                    return [...current, message];
                });
                setTypingUsers((current) => {
                    const next = new Map(current);
                    next.delete(message.senderUserId);
                    return next;
                });
            },
            onTyping: (event: ChatTypingEvent) => {
                if (event.senderUserId === currentUser.userId) return;

                setTypingUsers((current) => {
                    const next = new Map(current);

                    if (event.typing) {
                        next.set(event.senderUserId, {
                            username: event.senderUsername,
                            lastSeenAt: Date.now(),
                        });
                    } else {
                        next.delete(event.senderUserId);
                    }

                    return next;
                });
            },
        });

        socketRef.current = socketClient;
        socketClient.connect();

        return () => {
            socketClient.disconnect();
        };
    }, [activeRoomId, currentUser]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, typingLabel]);

    useEffect(() => {
        const interval = window.setInterval(() => {
            setTypingUsers((current) => {
                const next = new Map(current);
                const now = Date.now();

                next.forEach((typingUser, userId) => {
                    if (now - typingUser.lastSeenAt > 3500) {
                        next.delete(userId);
                    }
                });

                return next;
            });
        }, 1200);

        return () => window.clearInterval(interval);
    }, []);

    function handleDraftChange(value: string) {
        setDraftMessage(value);
        setSelectedMentions((current) => current.filter((member) => value.toLowerCase().includes(`@${getMemberLabel(member).toLowerCase()}`)));

        if (!value.trim()) {
            stopTyping();
            return;
        }

        if (!isTypingRef.current) {
            isTypingRef.current = true;
        }

        socketRef.current?.sendTyping(true);

        if (typingStopTimerRef.current) {
            window.clearTimeout(typingStopTimerRef.current);
        }

        typingStopTimerRef.current = window.setTimeout(() => {
            stopTyping();
        }, 1200);
    }

    function stopTyping() {
        if (typingStopTimerRef.current) {
            window.clearTimeout(typingStopTimerRef.current);
            typingStopTimerRef.current = null;
        }

        if (isTypingRef.current) {
            socketRef.current?.sendTyping(false);
            isTypingRef.current = false;
        }
    }

    function handleMentionSelect(member: ChatRoomMember) {
        const token = getMentionToken(draftMessage);

        if (!token) return;

        const nextValue = `${draftMessage.slice(0, token.start)}@${getMemberLabel(member)} `;
        setDraftMessage(nextValue);
        setSelectedMentions((current) => {
            if (current.some((selectedMention) => selectedMention.userId === member.userId)) return current;
            return [...current, member];
        });
        inputRef.current?.focus();
    }

    function removeMention(userId: number) {
        setSelectedMentions((current) => current.filter((member) => member.userId !== userId));
    }

    function getMentionedUserIds(message: string) {
        const normalizedMessage = message.toLowerCase();
        return selectedMentions
            .filter((member) => normalizedMessage.includes(`@${getMemberLabel(member).toLowerCase()}`))
            .map((member) => member.userId);
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();

        const message = draftMessage.trim();

        if (!message || !activeRoomId || isSendingMessage) return;

        stopTyping();
        setError("");
        setIsSendingMessage(true);

        try {
            const savedMessage = await sendChatMessage(activeRoomId, message, getMentionedUserIds(message));

            setMessages((current) => {
                if (current.some((existing) => existing.messageId === savedMessage.messageId)) return current;
                return [...current, savedMessage];
            });
            setDraftMessage("");
            setSelectedMentions([]);
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setIsSendingMessage(false);
        }
    }

    return (
        <section className="chat-page">
            <aside className="chat-sidebar">
                <div className="chat-sidebar-header">
                    <p className="request-page-kicker">Messaging</p>
                    <h1 className="chat-title">Chat</h1>
                </div>

                <div className="chat-room-list">
                    {isLoadingRooms ? (
                        <div className="chat-muted">Loading rooms...</div>
                    ) : rooms.length === 0 ? (
                        <div className="chat-muted">No chat rooms found.</div>
                    ) : (
                        rooms.map((room) => (
                            <button
                                className={room.roomId === activeRoomId ? "chat-room chat-room-active" : "chat-room"}
                                key={room.roomId}
                                type="button"
                                onClick={() => setActiveRoomId(room.roomId)}
                            >
                                <span className="chat-room-avatar">{room.roomName.slice(0, 2).toUpperCase()}</span>
                                <span>
                                    <span className="chat-room-name">{room.roomName}</span>
                                    <span className="chat-room-type">{room.roomType.replace("_", " ").toLowerCase()}</span>
                                </span>
                            </button>
                        ))
                    )}
                </div>
            </aside>

            <div className="chat-panel">
                <header className="chat-panel-header">
                    <div className="chat-room-avatar chat-room-avatar-large">
                        {(activeRoom?.roomName ?? "CH").slice(0, 2).toUpperCase()}
                    </div>
                    <div>
                        <h2 className="chat-panel-title">{activeRoom?.roomName ?? "Chat"}</h2>
                        <p className="chat-panel-status">
                            {socketStatus === "connected" ? "Online" : socketStatus === "connecting" ? "Connecting" : "Offline"}
                        </p>
                    </div>
                </header>

                {error && <p className="request-error chat-error" role="alert">{error}</p>}

                <div className="chat-messages">
                    {isLoadingMessages ? (
                        <div className="chat-muted">Loading messages...</div>
                    ) : messages.length === 0 ? (
                        <div className="chat-empty-state">No messages yet</div>
                    ) : (
                        messages.map((message) => {
                            const isOwnMessage = message.senderUserId === currentUser?.userId;

                            return (
                                <article
                                    className={isOwnMessage ? "chat-message chat-message-own" : "chat-message"}
                                    key={message.messageId}
                                >
                                    {!isOwnMessage && (
                                        <span className="chat-message-sender">
                                            {message.senderUsername} · {getRoleLabel(message.senderRole)}
                                        </span>
                                    )}
                                    <p>{renderMessageText(message)}</p>
                                    <time>{formatMessageTime(message.createdAt)}</time>
                                </article>
                            );
                        })
                    )}

                    {typingLabel && (
                        <div className="chat-typing">
                            <span>{typingLabel}</span>
                            <span className="chat-typing-dots" aria-hidden="true">
                                <span />
                                <span />
                                <span />
                            </span>
                        </div>
                    )}

                    <div ref={messagesEndRef} />
                </div>

                <form className="chat-compose" onSubmit={handleSubmit}>
                    <div className="chat-compose-field">
                        {mentionSuggestions.length > 0 && (
                            <div className="chat-mention-menu">
                                {mentionSuggestions.map((member) => (
                                    <button
                                        key={member.userId}
                                        type="button"
                                        onClick={() => handleMentionSelect(member)}
                                    >
                                        <span>{getMemberLabel(member)}</span>
                                        <span>{getRoleLabel(member.userRole)}</span>
                                    </button>
                                ))}
                            </div>
                        )}

                        <input
                            ref={inputRef}
                            value={draftMessage}
                            onChange={(event) => handleDraftChange(event.target.value)}
                            placeholder="Type a message"
                            disabled={!activeRoomId || isSendingMessage}
                        />

                        {selectedMentions.length > 0 && (
                            <div className="chat-selected-mentions">
                                {selectedMentions.map((member) => (
                                    <span key={member.userId}>
                                        @{getMemberLabel(member)}
                                        <button type="button" onClick={() => removeMention(member.userId)} aria-label={`Remove ${getMemberLabel(member)}`}>
                                            x
                                        </button>
                                    </span>
                                ))}
                            </div>
                        )}
                    </div>
                    <button type="submit" disabled={!activeRoomId || !draftMessage.trim() || isSendingMessage}>
                        {isSendingMessage ? "Sending" : "Send"}
                    </button>
                </form>
            </div>
        </section>
    );
}
