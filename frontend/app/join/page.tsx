"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";

export default function UserJoinForm() {
    const searchParams = useSearchParams();
    const router = useRouter();

    const email = searchParams.get("email") || "";
    const identify = searchParams.get("identify") || "";
    const provider = searchParams.get("provider") || "NAVER"; // 기본값 NAVER

    const [formData, setFormData] = useState({
        name: "",
        nickname: "",
        sex: "",
        age: "",
        email: email,   // URL에서 가져온 값 설정
        identify: identify,
        provider: provider
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSexChange = (sex: string) => {
        setFormData((prev) => ({ ...prev, sex }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log("회원가입 데이터:", formData);

        try {
            const response = await fetch("/api/v1/user/join", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            if (!response.ok) throw new Error("회원가입 실패");

            console.log("회원가입 성공!");
            alert("회원가입이 완료되었습니다!");
            router.push("/login");
        } catch (error) {
            console.error("회원가입 오류:", error);
            alert("회원가입 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-3xl font-bold mb-6">회원가입</h1>
            <form onSubmit={handleSubmit} className="bg-gray-800 text-white p-6 rounded-lg shadow-lg w-96">
                {/* 이름 */}
                <div className="mb-4">
                    <label className="block mb-2">이름</label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-700 text-white"
                        required
                    />
                </div>

                {/* 닉네임 */}
                <div className="mb-4">
                    <label className="block mb-2">닉네임</label>
                    <input
                        type="text"
                        name="nickname"
                        value={formData.nickname}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-700 text-white"
                        required
                    />
                </div>

                {/* 성별 */}
                <div className="mb-4">
                    <label className="block mb-2">성별</label>
                    <div className="flex gap-4">
                        <button
                            type="button"
                            className={`p-2 w-1/2 rounded-md ${formData.sex === "MALE" ? "bg-blue-500" : "bg-gray-600"}`}
                            onClick={() => handleSexChange("MALE")}
                        >
                            ⭕ Male
                        </button>
                        <button
                            type="button"
                            className={`p-2 w-1/2 rounded-md ${formData.sex === "FEMALE" ? "bg-pink-500" : "bg-gray-600"}`}
                            onClick={() => handleSexChange("FEMALE")}
                        >
                            ⭕ Female
                        </button>
                    </div>
                </div>

                {/* 생년월일 */}
                <div className="mb-4">
                    <label className="block mb-2">생년월일</label>
                    <input
                        type="date"
                        name="age"
                        value={formData.age}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-700 text-white"
                        required
                    />
                </div>

                {/* 회원가입 버튼 */}
                <button type="submit" className="w-full bg-white text-black p-2 rounded-md font-bold mt-4">
                    회원가입
                </button>
            </form>
        </div>
    );
}