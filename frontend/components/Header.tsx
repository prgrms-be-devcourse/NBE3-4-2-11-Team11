"use client"; // ✅ 클라이언트 컴포넌트에서만 실행

import Link from "next/link";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore"; // ✅ Zustand 사용

const Header = () => {
  const { isLoggedIn, login, logout } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    // ✅ 서버에서는 실행되지 않도록 보장
    if (typeof window !== "undefined") {
      // 🔥 localStorage 제거 & 상태 관리
      const tokenExists = !!document.cookie.includes("accessToken");
      if (tokenExists) {
        login();
      }
    }
  }, [login]);

  return (
      <header className="bg-gray-900 text-white py-4 px-8 flex justify-between items-center">
        <div className="text-xl font-bold">
          <Link href="/" className="hover:text-gray-400">POFO</Link>
        </div>
        <nav>
          <ul className="flex space-x-6">
            <li>
              <Link href="/notice" className="hover:text-gray-400">공지사항</Link>
            </li>
            <li>
              <Link href="/contact" className="hover:text-gray-400">문의하기</Link>
            </li>
            <li>
              {isLoggedIn ? (
                  <button onClick={logout} className="hover:text-red-400">로그아웃</button>
              ) : (
                  <Link href="/login" className="hover:text-gray-400">로그인</Link>
              )}
            </li>
          </ul>
        </nav>
      </header>
  );
};

export default Header;
