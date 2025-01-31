"use client"
import React from "react";
import "../globals.css"
import styles from "login.module.css";

const login = () => {
    return (
        <div className={styles.loginContainer}>
            <div className={styles.loginBox}>
                <h2 className={styles.title}>DEVELOPERS'</h2>
                <button className={`${styles.loginButton} ${styles.google}`}>
                    <span className={styles.icon}>N</span> Continue with Naver
                </button>
                <button className={`${styles.loginButton} ${styles.kakao}`}>
                    <span className={styles.icon}>K</span> Continue with Kakao
                </button>
                <p className={styles.adminLogin}>관리자 로그인</p>
            </div>
        </div>
    );
};

export default login;