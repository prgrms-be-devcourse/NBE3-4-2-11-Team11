"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";

export default function UserJoinForm() {
    const searchParams = useSearchParams();
    const router = useRouter();

    const email = searchParams.get("email") || "";
    const identify = searchParams.get("identify") || "";
    const provider = searchParams.get("provider");

    const [formData, setFormData] = useState({
        name: "",
        nickname: "",
        sex: "",
        age: "",
        jobInterest:"",
        userStatus:"",
        email: email,   // URL에서 가져온 값 설정
        identify: identify,
        provider: provider
    });

    const [existingEmail, setExistingEmail] = useState<string | null>(null);
    const [showMergePrompt, setShowMergePrompt] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSexChange = (sex: string) => {
        setFormData((prev) => ({ ...prev, sex }));
    };

    const handleUserSatusChange = (userStatus: string) => {
        setFormData((prev) => ({ ...prev, userStatus }));
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await fetch("/api/v1/user/join", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            const result = await response.json();

            if (response.status === 202) {
                // ✅ 기존 계정이 존재하면 confirm 창 띄우기
                const isConfirmed = window.confirm(
                    `⚠️ 기존 계정(${result.data.email})이 존재합니다.\n\n본인 계정이 맞다면 통합을 진행해주세요.`
                );

                if (isConfirmed) {
                    // ✅ 이메일 인증 요청 추가 (자동 전송)
                    fetch(`/api/v1/user/send-verification/${result.data.email}`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                    })
                        .then((res) => res.json())
                    router.push(`/join/verify_email?email=${result.data.email}&provider=${provider}&identify=${identify}`);
                } else {
                    try {
                        const registerResponse = await fetch("/api/v1/user/join/force", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(formData),
                        });

                        const registerResult = await registerResponse.json();

                        console.log(registerResult);

                        if (registerResponse.status === 200) {
                            alert("회원가입이 완료되었습니다!");
                            router.push("/login");
                        } else {
                            throw new Error("회원가입 실패");
                        }

                    }catch (error) {
                        console.log(error);
                        alert("회원가입 중 오류 발생")
                    }
                }
                return;
            }

            if (response.status === 200) {
                console.log("✅ 회원가입 성공!");
                alert("회원가입이 완료되었습니다!");
                router.push("/login");
            } else {
                throw new Error("회원가입 실패");
            }
        } catch (error) {
            console.error("회원가입 오류:", error);
            alert("회원가입 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-3xl font-bold mb-6">회원가입</h1>

            {/* ✅ 기존 계정이 있을 경우 안내 메시지 */}
            {showMergePrompt && (
                <div className="bg-yellow-300 p-4 mb-4 text-black rounded-md shadow-md">
                    <p>⚠ 기존 계정이 존재합니다.</p>
                    <p>이전 계정 이메일: {existingEmail}</p>
                    <p>본인 계정이 맞다면 인증을 진행해주세요.</p>
                    <button
                        className="bg-blue-500 text-white px-4 py-2 rounded-md mt-2"
                        onClick={() => router.push(`/verify-email?email=${existingEmail}`)}
                    >
                        인증 후 통합하기
                    </button>
                </div>
            )}

            {/* ✅ 기존 계정 여부와 관계없이 폼은 항상 표시 */}
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

                {/* 관심 직종 */}
                <div className="mb-4">
                    <label className="block mb-2">관심 직종</label>
                    <input
                        type="text"
                        name="jobInterest"
                        value={formData.jobInterest}
                        onChange={handleChange}
                        className="w-full p-2 rounded-md bg-gray-700 text-white"
                        required
                    />
                </div>

                {/* 취업 여부 */}
                <div className="mb-4">
                    <label className="block mb-2">취업 여부</label>
                    <div className="flex gap-4">
                        <button
                            type="button"
                            className={`p-2 w-1/3 rounded-md ${formData.userStatus === "UNEMPLOYED" ? "bg-red-500" : "bg-gray-600"}`}
                            onClick={() => handleUserSatusChange("UNEMPLOYED")}
                        >
                            ⭕ 구직중
                        </button>
                        <button
                            type="button"
                            className={`p-2 w-1/3 rounded-md ${formData.userStatus === "EMPLOYED" ? "bg-blue-500" : "bg-gray-600"}`}
                            onClick={() => handleUserSatusChange("EMPLOYED")}
                        >
                            ⭕ 재직중
                        </button>
                        <button
                            type="button"
                            className={`p-2 w-1/3 rounded-md ${formData.userStatus === "STUDENT" ? "bg-yellow-500" : "bg-gray-600"}`}
                            onClick={() => handleUserSatusChange("STUDENT")}
                        >
                            ⭕ 학부생
                        </button>
                    </div>
                </div>

                {/* 회원가입 버튼 */}
                <button type="submit" className="w-full bg-white text-black p-2 rounded-md font-bold mt-4">
                    회원가입
                </button>
            </form>
        </div>
    );
}
