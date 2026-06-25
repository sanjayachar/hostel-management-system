import { api } from "../../lib/api";

export type ChatRoom = {
    roomId: number;
    roomName: string;
    roomType: string;
    createdBy?: number | null;
    createdAt: string;
};

export type ChatMessage = {
    messageId: number;
    roomId: number;
    senderUserId: number;
    senderUsername: string;
    senderRole: string;
    message: string;
    mentions: ChatMention[];
    createdAt: string;
};

export type ChatMention = {
    mentionId: number;
    userId: number;
    username: string;
    role: string;
    readStatus: boolean;
};

export type ChatRoomMember = {
    userId: number;
    username: string;
    userRole: string;
};

export type ChatTypingEvent = {
    roomId: number;
    senderUserId: number;
    senderUsername: string;
    senderRole: string;
    typing: boolean;
    eventTime: string;
};

type ChatMessagePage = {
    content: ChatMessage[];
};

export async function getChatRooms() {
    const response = await api.get<ChatRoom[]>("/chat-service-api/chat/rooms");
    return response.data;
}

export async function getChatMessages(roomId: number) {
    const response = await api.get<ChatMessagePage>(`/chat-service-api/chat/rooms/${roomId}/messages?page=0&size=100`);
    return response.data.content;
}

export async function getChatRoomMembers(roomId: number) {
    const response = await api.get<ChatRoomMember[]>(`/chat-service-api/chat/rooms/${roomId}/members`);
    return response.data;
}

export async function sendChatMessage(roomId: number, message: string, mentionedUserIds: number[] = []) {
    const response = await api.post<ChatMessage>(`/chat-service-api/chat/rooms/${roomId}/messages`, { message, mentionedUserIds });
    return response.data;
}
