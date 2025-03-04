"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";
import { useRouter } from "next/navigation";

const Header = () => {
  const router = useRouter();
  const { isLoggedIn, role, logout, checkAuthStatus } = useAuthStore();
  const [hasChecked, setHasChecked] = useState(false);

  useEffect(() => {
    if (!hasChecked) {
      checkAuthStatus();
      setHasChecked(true);
    }
    const syncAuthState = () => {
      checkAuthStatus();
    };

    window.addEventListener("authChange", syncAuthState);
    return () => window.removeEventListener("authChange", syncAuthState);
  }, [hasChecked, checkAuthStatus]);

  const handleLogout = async () => {
    await logout();
    router.replace("/login");
  };

  // localStorage fallback: 만약 store에 role이 없으면 localStorage에서 확인
  const adminRole =
    role ||
    (typeof window !== "undefined" ? localStorage.getItem("role") : null);

  return (
    <header className="bg-gray-900 text-white py-4 px-8 flex justify-between items-center">
      <div className="text-xl font-bold">
        <Link href="/" className="hover:text-gray-400">
          POFO
        </Link>
      </div>
      <nav>
        <ul className="flex space-x-6">
          <li>
            <Link href="/notice" className="hover:text-gray-400">
              공지사항
            </Link>
          </li>
          <li className="relative group">
            <Link href="/mypage" className="hover:text-gray-400">
              마이페이지
            </Link>
            <ul className="absolute left-0 mt-2 w-32 bg-gray-800 text-white opacity-0 group-hover:opacity-100 transition-opacity">
              <li className="px-4 py-2 hover:bg-gray-700">
                <Link href="/mypage/resume">이력서</Link>
              </li>
              <li className="px-4 py-2 hover:bg-gray-700">
                <Link href="/mypage/projects">프로젝트</Link>
              </li>
              <li className="px-4 py-2 hover:bg-gray-700">
                <Link href="/board">게시판</Link>
              </li>
            </ul>
          </li>
          <li>
            <Link href="/inquiry" className="hover:text-gray-400">
              문의하기
            </Link>
          </li>
          {/* 관리자로 로그인한 경우에만 관리자 대시보드 링크 렌더링 */}
          {isLoggedIn && adminRole === "admin" && (
            <li>
              <Link href="/admin/dashboard" className="hover:text-gray-400">
                관리자 대시보드
              </Link>
            </li>
          )}
          <li>
            {isLoggedIn ? (
              <button onClick={handleLogout} className="hover:text-red-400">
                로그아웃
              </button>
            ) : (
              <Link href="/login" className="hover:text-gray-400">
                로그인
              </Link>
            )}
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
