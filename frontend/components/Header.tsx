"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";

const Header = () => {
  const { isLoggedIn, login, logout } = useAuthStore();
  const [hasMounted, setHasMounted] = useState(false); // ✅ Hydration 방지용 상태

  useEffect(() => {
    setHasMounted(true); // ✅ 클라이언트에서 마운트 후 상태 업데이트
  }, []);

  if (!hasMounted) return null; // 🔥 서버 렌더링 시 빈 화면 유지하여 Hydration 에러 방지

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
