"use client";

import { useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuthStore } from "@/store/authStore";

const NaverCallback = () => {
    const router = useRouter();
    const params = useSearchParams();
    const { login } = useAuthStore();

    useEffect(() => {
        const code = params.get("code");
        const state = params.get("state");

        if (code && state) {
            fetch(`/api/v1/user/naver/login/process?code=${code}&state=${state}`)
                .then((res) => res.json())
                .then((data) => {
                    if (data.resultCode === "200") {
                        login(); // ✅ Zustand 사용하여 로그인 상태 변경
                        router.push("/");
                    } else {
                        router.push("/login?error=auth_failed");
                    }
                })
                .catch(() => router.push("/login?error=auth_failed"));
        }
    }, [params, login, router]);

    return <div>네이버 로그인 처리 중...</div>;
};

export default NaverCallback;
