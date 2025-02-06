"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function NaverCallback() {
    const router = useRouter();

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");
        const state = params.get("state");

        if (!code || !state) {
            console.log("❌ 네이버 로그인 콜백 파라미터 누락!");
            router.push("/login?error=missing_params");
            return;
        }

        console.log("✅ 네이버 로그인 콜백 수신: ", code, state);

        // ✅ Next.js에서 직접 백엔드의 로그인 API를 호출
        const handleNaverLogin = async () => {
            try {
                const response = await fetch(
                    `/api/v1/user/naver/login/process?code=${code}&state=${state}`,
                    {
                        method: "GET",
                        credentials: "include",
                        headers: {
                            "Content-Type": "application/json"
                        }
                    }
                );

                if (!response.ok) {
                    throw new Error(`네이버 로그인 실패: ${response.status}`);
                }

                const data = await response.json();
                console.log("✅ 로그인 성공", data);

                if (data.resultCode === "200") {
                    if (data.token) {
                        localStorage.setItem("access_token", data.token);
                    }
                    router.push("/");
                } else if (data.resultCode === "201") {
                    console.log("📌 네이버 로그인 후 회원가입 필요", data);

                    if (!data.data?.email || !data.data?.identify) {
                        console.error("⚠️ 회원가입에 필요한 정보가 부족합니다:", data);
                        router.push("/login?error=missing_user_info");
                        return;
                    }


                    router.push(`/join?email=${data.data.email}&identify=${data.data.identify}&provider=NAVER`);
                }

            } catch (error) {
                console.error("❌ 네이버 로그인 실패: ", error);
                router.push("/login?error=naver_login_failed");
            }
        };

        handleNaverLogin();
    }, [router]);

    return <div>네이버 로그인 중...</div>;
}
