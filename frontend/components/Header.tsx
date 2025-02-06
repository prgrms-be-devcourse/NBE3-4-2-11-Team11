"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";

const Header = () => {
  const { isLoggedIn, login, logout } = useAuthStore();
  const [hasMounted, setHasMounted] = useState(false);

  useEffect(() => {
    setHasMounted(true);

    // ✅ localStorage에서 accessToken이 있으면 로그인 유지
    const token = localStorage.getItem("accessToken");
    if (token) login(token);
  }, [login]);

  if (!hasMounted) return null; // Hydration 오류 방지

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
              <Link href="/mypage" className="hover:text-gray-400">마이페이지</Link>
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
