// /utils/token.ts
import {getAccessToken} from "@/utils/token";

export const decodeJWT = (token: string): { exp?: number } | null => {
    try {
        const payloadBase64 = token.split(".")[1]; // JWT 구조: header.payload.signature
        const decodedPayload = atob(payloadBase64); // Base64 디코딩
        return JSON.parse(decodedPayload);
    } catch (error) {
        console.error("❌ JWT 디코딩 실패:", error);
        return null;
    }
};
