"use client";

import {useEffect, useState} from "react";
import Link from "next/link";
import {useSearchParams} from "next/navigation";
import { useAuthStore } from "@/store/authStore"; // ✅ useAuthStore import 추가

export default function LoginPage() {
    const [loading, setLoading] = useState(false);
    //const [lastLoginProvider, setLastLoginProvider] = useState<string | null>(null); // ✅ 상태 추가
    const [lastLoginProvider, setLastLoginProvider] = useState("NULL"); // ✅ 상태 추가
    const searchParams = useSearchParams();
    const error = searchParams.get("error");
    const provider = searchParams.get("provider")?.toUpperCase() ;


 // ✅ useAuthStore에서 login 함수 가져오기
    const { login } = useAuthStore();


    //  네이버 로그인 정보
    const NAVER_CLIENT_ID  = process.env.NEXT_PUBLIC_CLIENT_ID;
    const NAVER_REDIRECT_URI = process.env.NEXT_PUBLIC_REDIRECT_URI;
    const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${NAVER_CLIENT_ID}&redirect_uri=${NAVER_REDIRECT_URI}&state=12345`;

    let providerName = "";

    if (lastLoginProvider  === "NAVER") {
        providerName = "네이버";
    } else if (lastLoginProvider  === "KAKAO") {
        providerName = "카카오";
    } else if (lastLoginProvider  === "GOOGLE") {
        providerName = "구글";
    }

    const handleLogin = async (provider: string) => {
        try {
            setLoading(true);

            if (provider === "NAVER") {
                window.location.href = NAVER_AUTH_URL;
            } else if (provider === "ADMIN") {
                window.location.href = "/api/v1/admin/login";
            } else {
                window.location.href = `/api/v1/user/${provider}/login`;
                await login("user"); // ✅ 로그인 상태 저장
            }
        } catch (error) {
            console.error("❌ 로그인 중 오류 발생:", error);
        } finally {
            setLoading(false);
        }
    };



    useEffect(() => {
        // ✅ 마지막 로그인했던 플랫폼 가져오기
        const fetchLastLoginProvider = async () => {
            try {
                const response = await fetch("/api/v1/user/last-login-provider", {
                    method: "GET",
                    credentials: "include", // ✅ 쿠키 포함 요청
                });
                const data = await response.json();

                setLastLoginProvider(data.lastLoginProvider);

            } catch (error) {
                console.error("❌ 마지막 로그인 제공자 정보를 가져오지 못함:", error);
            }
        };

        fetchLastLoginProvider();
    }, []);


    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-2xl font-bold mb-6">로그인</h1>

            {/*  에러 메시지 표시 */}
            {error && provider && (
                <div className="bg-red-500 text-white px-4 py-2 rounded-md mb-4">
                    ⚠️ {provider} 로그인 실패: {error === "missing_params"
                    ? "필수 로그인 정보가 없습니다."
                    : `${provider} 로그인 중 오류가 발생했습니다.`}
                </div>
            )}

            {/* ✅ 마지막 로그인 플랫폼 안내 메시지 추가 */}
            {lastLoginProvider !== "NONE" && (
                <>
                    <p className="mb-2 text-gray-700">📝 마지막으로 {providerName} 계정으로 로그인했어요!</p>
                    <button
                        onClick={() => handleLogin(lastLoginProvider.toLowerCase())}
                        disabled={loading}
                        className="bg-blue-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-600 mb-4 w-64"
                    >
                        {loading ? "로그인 중..." : `${providerName} 계정으로 빠른 로그인`}
                    </button>
                    <hr className="my-6 border-t border-gray-600 w-64" />
                </>
            )}

            {/* 일반 로그인 버튼들 (기존 방식 유지) */}

            <button
                onClick={() => handleLogin("naver")}
                disabled={loading}
                className="bg-green-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-green-600 mb-4 w-64"
            >
                {loading ? "로그인 중..." : "네이버 로그인"}
            </button>

            <button
                onClick={() => handleLogin("google")}
                disabled={loading}
                className="bg-red-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-red-600 mb-4 w-64"
            >
                {loading ? "로그인 중..." : "구글 로그인"}
            </button>

            <button
                onClick={() => handleLogin("kakao")}
                disabled={loading}
                className="bg-yellow-400 text-black px-6 py-3 rounded-lg shadow-md hover:bg-yellow-500 w-64"
            >
                {loading ? "로그인 중..." : "카카오 로그인"}
            </button>


             <button
                 onClick={() => window.location.href = "/admin/login"}
                 className="bg-blue-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-600 mt-6 w-64"
             >
                 관리자 로그인
             </button>

            <p className="mt-6 text-gray-600">
                <Link href="/" className="text-blue-500 hover:underline">
                    홈으로 돌아가기
                </Link>
            </p>
        </div>
    );
}
