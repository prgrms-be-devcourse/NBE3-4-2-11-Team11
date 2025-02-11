"use client";

import { useEffect } from "react";
import {getRefreshToken, isAccessTokenExpired} from "../utils/token";
import { useAuthStore } from "../store/authStore";
import {decodeJWT} from "@/utils/decodeJWT";

// const refreshToken = getRefreshToken();
const useTokenRefresh = () => {

    useEffect(() => {
        const interval = setInterval(async () => {
            const currentRefreshToken = getRefreshToken();

            if (currentRefreshToken) {
                const decoded = decodeJWT(currentRefreshToken);
                console.log("🔍 디코딩된 Refresh Token:", decoded);
            } else {
                console.warn("❌ Refresh Token이 없습니다.");
            }
            if (isAccessTokenExpired()) {
                console.log("🔄 Access Token 만료 감지 → Refresh Token 요청 실행");
                await useAuthStore.getState().refreshAccessToken();
            }
        }, 20000); // ✅ 20초마다 실행 (필요 시 조정 가능)

        return () => clearInterval(interval);
    }, []);
};

export default useTokenRefresh;
