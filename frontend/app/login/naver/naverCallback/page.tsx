"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore"; // ✅ Zustand 스토어 불러오기

export default function NaverCallback() {
    const router = useRouter();
    const { login } = useAuthStore(); // ✅ 로그인 함수 가져오기

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");
        const state = params.get("state");

        if (!code || !state) {
            console.log("❌ 네이버 로그인 콜백 파라미터 누락");
            router.push("/login?error=missing_params");
            return;
        }

        console.log("✅ 네이버 로그인 콜백 수신:", code, state);

        const handleNaverLogin = async () => {
            try {
                const response = await fetch(`/api/v1/user/naver/login/process?code=${code}&state=${state}`);
                const data = await response.json();

                if (data.resultCode === "200") {
                    console.log("✅ 로그인 성공:", data);
                    login(data.data.token); // ✅ Zustand의 로그인 함수 호출 (토큰 저장 & 상태 업데이트)
                    router.push("/");
                } else {
                    console.error("❌ 로그인 실패:", data);
                    router.push("/login");
                }
            } catch (error) {
                console.error("❌ 네이버 로그인 오류:", error);
            }
        };

        handleNaverLogin();
    }, [router, login]);

    return <div>네이버 로그인 중...</div>;
}
