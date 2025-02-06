"use client";

import { useState } from "react";
import Link from "next/link";
import {useSearchParams} from "next/navigation";

export default function LoginPage() {
    const [loading, setLoading] = useState(false);
    const searchParams = useSearchParams();
    const error = searchParams.get("error");
    const provider = searchParams.get("provider")?.toUpperCase() ;

    //  네이버 로그인 정보
    const NAVER_CLIENT_ID  = process.env.NEXT_PUBLIC_CLIENT_ID;
    const NAVER_REDIRECT_URI = process.env.NEXT_PUBLIC_REDIRECT_URI;
    const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${NAVER_CLIENT_ID}&redirect_uri=${NAVER_REDIRECT_URI}&state=12345`;

    const handleLogin = (provider: string) => {
        setLoading(true);

        if(provider === "NAVER") {
            window.location.href = NAVER_AUTH_URL;
        } else {
            window.location.href = `/api/v1/user/${provider}/login`; // 백엔드 OAuth 로그인 요청
        }
    };

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

            <p className="mt-6 text-gray-600">
                <Link href="/" className="text-blue-500 hover:underline">
                    홈으로 돌아가기
                </Link>
            </p>
        </div>
    );
}
