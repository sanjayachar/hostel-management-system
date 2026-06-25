import type { ChatMessage, ChatTypingEvent } from "./chatApi";

type ChatSocketOptions = {
    token: string;
    roomId: number;
    onMessage: (message: ChatMessage) => void;
    onTyping: (event: ChatTypingEvent) => void;
    onStatusChange: (status: ChatSocketStatus) => void;
    onError: (message: string) => void;
};

export type ChatSocketStatus = "connecting" | "connected" | "disconnected";

function createFrame(command: string, headers: Record<string, string>, body = "") {
    const headerLines = Object.entries(headers).map(([key, value]) => `${key}:${value}`);
    const headerBlock = [command, ...headerLines].join("\n");
    return `${headerBlock}\n\n${body}\0`;
}

function parseFrame(rawFrame: string) {
    const separatorIndex = rawFrame.indexOf("\n\n");
    const headerBlock = separatorIndex >= 0 ? rawFrame.slice(0, separatorIndex) : rawFrame;
    const body = separatorIndex >= 0 ? rawFrame.slice(separatorIndex + 2) : "";
    const [command, ...headerLines] = headerBlock.split("\n").filter(Boolean);
    const headers: Record<string, string> = {};

    headerLines.forEach((line) => {
        const splitAt = line.indexOf(":");

        if (splitAt >= 0) {
            headers[line.slice(0, splitAt)] = line.slice(splitAt + 1);
        }
    });

    return { command, headers, body };
}

function getWebSocketUrl() {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    return `${protocol}//${window.location.host}/chat-service-api/ws-chat-native`;
}

export class ChatSocketClient {
    private socket?: WebSocket;
    private buffer = "";
    private status: ChatSocketStatus = "disconnected";
    private readonly options: ChatSocketOptions;

    constructor(options: ChatSocketOptions) {
        this.options = options;
    }

    connect() {
        this.updateStatus("connecting");
        this.socket = new WebSocket(getWebSocketUrl());

        this.socket.onopen = () => {
            this.sendRaw(createFrame("CONNECT", {
                Authorization: `Bearer ${this.options.token}`,
                "accept-version": "1.2",
                "heart-beat": "10000,10000",
            }));
        };

        this.socket.onmessage = (event) => {
            this.handleSocketMessage(String(event.data));
        };

        this.socket.onerror = () => {
            this.options.onError("Chat connection failed.");
        };

        this.socket.onclose = () => {
            this.updateStatus("disconnected");
        };
    }

    disconnect() {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.sendRaw(createFrame("DISCONNECT", {}, ""));
        }

        this.socket?.close();
        this.updateStatus("disconnected");
    }

    sendMessage(message: string, mentionedUserIds: number[] = []) {
        this.sendJson(`/app/chat/rooms/${this.options.roomId}/send`, { message, mentionedUserIds });
    }

    sendTyping(typing: boolean) {
        this.sendJson(`/app/chat/rooms/${this.options.roomId}/typing`, { typing });
    }

    private handleSocketMessage(chunk: string) {
        if (chunk === "\n") return;

        this.buffer += chunk;
        let frameEnd = this.buffer.indexOf("\0");

        while (frameEnd >= 0) {
            const rawFrame = this.buffer.slice(0, frameEnd);
            this.buffer = this.buffer.slice(frameEnd + 1);
            this.handleFrame(rawFrame);
            frameEnd = this.buffer.indexOf("\0");
        }
    }

    private handleFrame(rawFrame: string) {
        if (!rawFrame.trim()) return;

        const frame = parseFrame(rawFrame);

        if (frame.command === "CONNECTED") {
            this.updateStatus("connected");
            this.subscribe(`/topic/chat/rooms/${this.options.roomId}`, "messages");
            this.subscribe(`/topic/chat/rooms/${this.options.roomId}/typing`, "typing");
            return;
        }

        if (frame.command === "MESSAGE") {
            const destination = frame.headers.destination ?? "";

            if (destination.endsWith("/typing")) {
                this.options.onTyping(JSON.parse(frame.body) as ChatTypingEvent);
            } else {
                this.options.onMessage(JSON.parse(frame.body) as ChatMessage);
            }

            return;
        }

        if (frame.command === "ERROR") {
            this.options.onError(frame.body || "Chat server rejected the connection.");
        }
    }

    private subscribe(destination: string, idSuffix: string) {
        this.sendRaw(createFrame("SUBSCRIBE", {
            id: `${idSuffix}-${this.options.roomId}`,
            destination,
            ack: "auto",
        }));
    }

    private sendJson(destination: string, payload: unknown) {
        if (this.status !== "connected") return;

        this.sendRaw(createFrame("SEND", {
            destination,
            "content-type": "application/json",
        }, JSON.stringify(payload)));
    }

    private sendRaw(frame: string) {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.socket.send(frame);
        }
    }

    private updateStatus(status: ChatSocketStatus) {
        this.status = status;
        this.options.onStatusChange(status);
    }
}
