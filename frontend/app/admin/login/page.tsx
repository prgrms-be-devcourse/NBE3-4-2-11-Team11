// "use client";
//
// import { useState } from "react";
// import { useRouter } from "next/navigation";
// import { useAuthStore } from "@/store/authStore"; // 제공된 Zustand 스토어 사용
//
// export default function AdminLoginPage() {
//   const [username, setUsername] = useState("");
//   const [password, setPassword] = useState("");
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState("");
//   const router = useRouter();
//   const { login } = useAuthStore();
//
//   const handleAdminLogin = async () => {
//     setLoading(true);
//     setError("");
//
//     try {
//       const response = await fetch("http://localhost:8080/api/v1/admin/login", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ username, password }),
//       });
//
//       if (!response.ok) {
//         const errorData = await response.json();
//         throw new Error(errorData.message || "로그인 실패");
//       }
//
//       // 헤더에서 토큰 가져오기
//       const accessToken = response.headers
//         .get("Authorization")
//         ?.replace("Bearer ", "");
//       const refreshToken = response.headers.get("Refresh-Token");
//
//       if (!accessToken || !refreshToken) {
//         throw new Error("백엔드에서 토큰을 받지 못했습니다.");
//       }
//
//       // 로컬 스토리지에 토큰 저장
//       localStorage.setItem("accessToken", accessToken);
//       localStorage.setItem("refreshToken", refreshToken);
//
//       // Zustand 스토어에 로그인 상태 반영
//       login(accessToken);
//
//       // 대시보드 페이지로 이동
//       router.push("/admin/dashboard");
//     } catch (err: any) {
//       setError(err.message);
//     } finally {
//       setLoading(false);
//     }
//   };
//
//   return (
//     <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
//       <h1 className="text-2xl font-bold mb-6">관리자 로그인</h1>
//       {error && (
//         <div className="bg-red-500 text-white px-4 py-2 rounded-md mb-4">
//           ⚠️ {error}
//         </div>
//       )}
//       <input
//         type="text"
//         placeholder="아이디"
//         value={username}
//         onChange={(e) => setUsername(e.target.value)}
//         className="w-64 px-4 py-2 mb-4 border rounded-md"
//       />
//       <input
//         type="password"
//         placeholder="비밀번호"
//         value={password}
//         onChange={(e) => setPassword(e.target.value)}
//         className="w-64 px-4 py-2 mb-4 border rounded-md"
//       />
//       <button
//         onClick={handleAdminLogin}
//         disabled={loading}
//         className="bg-blue-600 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-700 w-64"
//       >
//         {loading ? "로그인 중..." : "관리자 로그인"}
//       </button>
//     </div>
//   );
// }
"use client";

import { useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuthStore } from "@/store/authStore";

export default function OAuthCallback() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login } = useAuthStore();

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
            },
          }
        );

        if (!response.ok) {
          throw new Error(`${provider} 로그인 실패: ${response.status}`);
        }

        const data = await response.json();
        console.log(`✅ ${provider} 로그인 성공`, data);

        if (data.resultCode === "200") {
          // 여기가 서버에서 내려주는 토큰 정보(JSON) 구조에 따라 달라집니다.
          // 예) data.data = { accessToken, refreshToken, ... }
          const { accessToken, refreshToken } = data.data;

          if (accessToken) {
            // ✅ Refresh Token도 함께 주어진다고 가정
            if (!refreshToken) {
              console.warn("⚠️ Refresh Token이 포함되지 않았습니다. 백엔드 응답 확인 필요");
            }

            // ✅ Zustand에 저장
            login(accessToken, refreshToken);

            router.push("/");
          } else {
            console.error("❌ JWT 토큰이 없습니다.");
            router.push("/login");
          }

        } else if (data.resultCode === "201") {
          console.log(`📌 ${provider} 로그인 후 회원가입 필요`, data);
          if (!data.data?.email || !data.data?.identify) {
            console.error("⚠️ 회원가입에 필요한 정보가 부족합니다:", data);
            router.push("/login?error=missing_user_info");
            return;
          }

          router.push(
            `/join?email=${data.data.email}&identify=${data.data.identify}&provider=${provider}`
          );
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
