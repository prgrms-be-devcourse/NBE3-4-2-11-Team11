"use client";

import Link from "next/link";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore"; // ✅ Zustand 스토어 가져오기

const Header = () => {
  const { isLoggedIn, login, logout } = useAuthStore(); // ✅ Zustand 스토어에서 상태 & 함수 가져오기
  const router = useRouter();

  useEffect(() => {
    // ✅ localStorage에서 토큰 확인하고 Zustand에 저장 (초기 로드 시)
    const token = localStorage.getItem("accessToken");
    if (token) {
      login(token); // Zustand 상태 업데이트
    }

    // ✅ localStorage 변경 감지 (다른 탭에서 로그아웃 시 자동 반영)
    const handleStorageChange = () => {
      const updatedToken = localStorage.getItem("accessToken");
      if (!updatedToken) {
        logout(); // Zustand 상태 업데이트
      }
    };

    window.addEventListener("storage", handleStorageChange);
    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, [login, logout]);

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
            <li className="relative group">
              <Link href="/mypage" className="hover:text-gray-400">마이페이지</Link>
              <ul className="absolute left-0 mt-2 w-32 bg-gray-800 text-white opacity-0 group-hover:opacity-100 transition-opacity">
                <li className="px-4 py-2 hover:bg-gray-700">
                  <Link href="/mypage/resume">이력서</Link>
                </li>
                <li className="px-4 py-2 hover:bg-gray-700">
                  <Link href="/mypage/projects">프로젝트</Link>
                </li>
                <li className="px-4 py-2 hover:bg-gray-700">
                  <Link href="/mypage/board">게시판</Link>
                </li>
              </ul>
            </li>
            <li>
              <Link href="/contact" className="hover:text-gray-400">문의하기</Link>
            </li>
            <li>
              {isLoggedIn ? (
                  // ✅ 로그인 상태면 로그아웃 버튼 표시
                  <button onClick={logout} className="hover:text-red-400">로그아웃</button>
              ) : (
                  // ✅ 로그아웃 상태면 로그인 버튼 표시
                  <Link href="/login" className="hover:text-gray-400">로그인</Link>
              )}
            </li>
          </ul>
        </nav>
      </header>
  );
};

export default Header;
