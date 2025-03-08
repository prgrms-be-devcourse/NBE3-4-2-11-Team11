"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function ResignPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [userData, setUserData] = useState<{ email: string } | null>(null);

    // ✅ 로그인한 유저 정보 가져오기
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/v1/user/me", {
                    method: "GET",
                    credentials: "include", // ✅ 인증 포함 요청
                });

                if (!response.ok) {
                    throw new Error("로그인 정보를 가져오지 못했습니다.");
                }

                const result = await response.json();
                setUserData(result.data);
            } catch (err) {
                setError(err instanceof Error ? err.message : "알 수 없는 오류 발생");
            }
        };

        fetchUserData();
    }, []);

    const handleResign = async () => {
        if (!window.confirm("정말 탈퇴하시겠습니까? 😢")) {
            return;
        }

        if (!userData) {
            setError("로그인 정보가 없습니다.");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await fetch("http://localhost:8080/api/v1/user/resign", {
                method: "DELETE",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                mode: "cors",
                body: JSON.stringify({ email: userData.email }), // ✅ email을 body에 포함
            });

            const result = await response.json();

            if (response.ok) {
                alert("회원 탈퇴가 완료되었습니다.");

                if(setUserData) setUserData(null);

                window.location.href = "/";
            } else {
                throw new Error(result.message || "회원 탈퇴 실패");
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : "알 수 없는 오류 발생");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-3xl font-bold text-red-600 mb-4">회원 탈퇴</h1>
            <p className="mb-4 text-gray-700">탈퇴 시 모든 정보가 삭제됩니다. 신중히 결정하세요.</p>

            {error && <p className="text-red-500">{error}</p>}

            <button
                onClick={handleResign}
                disabled={loading}
                className="bg-red-700 text-white px-6 py-2 rounded-md mt-4 disabled:opacity-50"
            >
                {loading ? "탈퇴 처리 중..." : "회원 탈퇴"}
            </button>

            <button
                onClick={() => router.back()}
                className="mt-4 bg-gray-500 text-white px-6 py-2 rounded-md"
            >
                취소하고 돌아가기
            </button>
        </div>
    );
}
