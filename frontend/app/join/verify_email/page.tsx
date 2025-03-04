"use client";

import { useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";

export default function VerifyEmailPage() {
    const searchParams = useSearchParams();
    const router = useRouter();
    const email = searchParams.get("email") || "";
    const provider = searchParams.get("provider");
    const identify = searchParams.get("identify");
    const [code, setCode] = useState("");

    /** ✅ 사용자 입력값을 서버에 전송하여 인증 */
    const handleVerify = async () => {
        try {
            const response = await fetch(`/api/v1/user/verify-code`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: email,
                    code: code,
                    provider: provider, // ✅ "KAKAO", "NAVER", "GOOGLE"
                    identify: identify, // ✅ 소셜 플랫폼에서 받은 고유 ID
                }),
            });
            const result = await response.json();

            if (response.ok) {
                alert("이메일 인증 완료! 로그인 페이지로 이동합니다.");
                router.push("/login");
            } else {
                alert(result.message);
            }
        } catch (error) {
            alert("서버 오류 발생");
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-2xl font-bold mb-4">이메일 인증</h1>

            <input
                type="text"
                placeholder="인증 코드 입력"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                className="p-2 border rounded-md"
            />
            <button
                onClick={handleVerify}
                className="mt-4 bg-blue-500 text-white px-4 py-2 rounded-md"
            >
                인증하기
            </button>
        </div>
    );
}
