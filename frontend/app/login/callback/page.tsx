"use client";

import { useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import {refreshAccessToken} from "@/utils/token"; // ✅ Access Token 갱신 함수 사용

export default function OAuthCallback() {
    const router = useRouter();
    const searchParams = useSearchParams();

    useEffect(() => {
        const provider = searchParams.get("provider")?.toUpperCase();
        const code = searchParams.get("code");
        const state = searchParams.get("state");

        if (!provider || !code) {
            console.error("❌ OAuth 로그인 콜백 파라미터 누락!");
            router.push("/login?error=missing_params");
            return;
        }

        console.log(`✅ ${provider} 로그인 콜백 수신: `, code, state);

        const handleOAuthLogin = async () => {
            try {
                const response = await fetch(
                    `/api/v1/user/${provider.toLowerCase()}/login/process?code=${code}&state=${state || ""}`,
                    {
                        method: "GET",
                        credentials: "include",
                        headers: {
                            "Content-Type": "application/json",
                        }
                    }
                );

                if (!response.ok) {
                    throw new Error(`${provider} 로그인 실패: ${response.status}`);
                }

                const data = await response.json();

                if (data.resultCode === "200") {

                    console.log(`✅ ${provider} 로그인 성공, AccessToken 쿠키 저장 완료`);

                    // ✅ AccessToken, RefreshToken은 쿠키에 저장되므로 별도 저장 불필요
                    const success = await refreshAccessToken();  //  자동 갱신 트리거


                    if (success) {
                        console.log("✅ Access Token 자동 갱신 성공, 메인 페이지로 이동");
                        router.push("/");
                    } else {
                        console.warn("⚠️ Access Token 자동 갱신 실패, 로그인 페이지로 이동");
                        router.push("/login");
                    }

                } else if (data.resultCode === "201") {
                    console.log(`📌 ${provider} 로그인 후 회원가입 필요`, data);
                    if (!data.data?.email || !data.data?.identify) {
                        console.error("⚠️ 회원가입에 필요한 정보가 부족합니다:", data);
                        router.push("/login?error=missing_user_info");
                        return;
                    }

                    router.push(`/join?email=${data.data.email}&identify=${data.data.identify}&provider=${provider}`);
                }

            } catch (error) {
                console.error(`❌ ${provider} 로그인 실패: `, error);
                router.push(`/login?error=${provider.toLowerCase()}_login_failed`);
            }
        };

        handleOAuthLogin();
    }, [router, searchParams]);

    return <div>로그인 처리 중...</div>;
}
