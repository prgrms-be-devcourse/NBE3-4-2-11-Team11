"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function MyPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [userData, setUserData] = useState({
        email: "",
        name: "",
        nickname: "",
        sex: "",
        age: "",
        jobInterest: "",
        userStatus: "",
    });

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                // ✅ 현재 로그인한 유저 정보를 먼저 가져오기
                const userResponse = await fetch("/api/v1/user/me", {
                    method: "GET",
                    credentials: "include", // ✅ 쿠키 기반 인증 포함
                });

                if (!userResponse.ok) {
                    throw new Error("로그인한 유저 정보를 가져올 수 없습니다.");
                }

                const userResult = await userResponse.json();
                const userEmail = userResult.data.email; // ✅ 로그인한 유저 이메일 추출

                // ✅ 유저 이메일이 없으면 중단
                if (!userEmail) {
                    throw new Error("이메일 정보가 없습니다.");
                }

                const response = await fetch(`/api/v1/user/mypage?email=${userEmail}`, {
                    method: "GET",
                    credentials: "include", // ✅ 인증된 사용자 요청
                });

                if (!response.ok) {
                    throw new Error("사용자 정보를 불러오는 데 실패했습니다.");
                }

                const data = await response.json();
                setUserData(data.data);
            } catch (error) {
                setError(error instanceof Error ? error.message : "알 수 없는 오류 발생");
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, []);

    if (loading) {
        return <div className="text-center mt-10">⏳ 로딩 중...</div>;
    }

    if (error) {
        return <div className="text-center mt-10 text-red-500">❌ {error}</div>;
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-3xl font-bold mb-6">마이페이지</h1>
            <div className="bg-white p-6 rounded-lg shadow-lg w-96 text-black">
                <p className="mb-2"><strong>이메일:</strong> {userData.email}</p>
                <p className="mb-2"><strong>이름:</strong> {userData.name}</p>
                <p className="mb-2"><strong>닉네임:</strong> {userData.nickname}</p>
                <p className="mb-2"><strong>성별:</strong> {userData.sex === "MALE" ? "남성" : "여성"}</p>
                <p className="mb-2"><strong>생년월일:</strong> {userData.age}</p>
                <p className="mb-2"><strong>관심 직종:</strong> {userData.jobInterest || "정보 없음"}</p>
                <p className="mb-2"><strong>취업 상태:</strong> {userData.userStatus}</p>
            </div>
            <div className="flex flex-row gap-4 mt-4">
                <button
                    onClick={() => router.push("/mypage/edit")}
                    className="bg-blue-500 text-white px-4 py-2 rounded-md"
                >
                    프로필 수정하기
                </button>

                <button
                    onClick={() => router.push("/mypage/resign")}
                    className="bg-red-700 text-white px-4 py-2 rounded-md"
                >
                    회원 탈퇴
                </button>
            </div>
        </div>
    );
}