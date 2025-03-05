"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

export default function EditProfilePage() {
    const router = useRouter();
    const [formData, setFormData] = useState({
        nickname: "",
        jobInterest: "",
        userStatus: "",
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/v1/user/me", {
                    method: "GET",
                    credentials: "include",
                });

                if (!response.ok) throw new Error("사용자 정보를 불러오는 데 실패했습니다.");

                const data = await response.json();
                setFormData({
                    nickname: data.data.nickname,
                    jobInterest: data.data.jobInterest || "",
                    userStatus: data.data.userStatus || "",
                });
            } catch (error) {
                setError(error instanceof Error ? error.message : "알 수 없는 오류 발생");
            }
        };

        fetchUserData();
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await fetch("/api/v1/user/edit", {
                method: "PATCH",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            if (!response.ok) throw new Error("프로필 수정에 실패했습니다.");

            alert("프로필이 수정되었습니다!");
            router.push("/mypage");
        } catch (error) {
            setError(error instanceof Error ? error.message : "알 수 없는 오류 발생");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-3xl font-bold mb-6">프로필 수정</h1>
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-lg w-96">
                {error && <p className="text-red-500">{error}</p>}

                {/* 닉네임 */}
                <div className="mb-4">
                    <label className="block mb-2">닉네임</label>
                    <input
                        type="text"
                        name="nickname"
                        value={formData.nickname}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-200"
                        required
                    />
                </div>

                {/* 관심 직종 */}
                <div className="mb-4">
                    <label className="block mb-2">관심 직종</label>
                    <input
                        type="text"
                        name="jobInterest"
                        value={formData.jobInterest}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-200"
                    />
                </div>

                {/* 취업 상태 */}
                <div className="mb-4">
                    <label className="block mb-2">취업 상태</label>
                    <select
                        name="userStatus"
                        value={formData.userStatus}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-200"
                    >
                        <option value="">선택하세요</option>
                        <option value="UNEMPLOYED">구직 중</option>
                        <option value="EMPLOYED">재직 중</option>
                        <option value="STUDENT">학부생</option>
                    </select>
                </div>

                <button type="submit" className="w-full bg-blue-500 text-white p-2 rounded-md font-bold">
                    {loading ? "수정 중..." : "프로필 수정"}
                </button>
            </form>
        </div>
    );
}
