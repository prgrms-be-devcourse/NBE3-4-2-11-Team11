"use client";

import React, { useMemo } from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface MonthlyRegistrationChartProps {
  userStats: { createdAt: string }[];
}

const MonthlyRegistrationChart: React.FC<MonthlyRegistrationChartProps> = ({ userStats }) => {
  // 월별 가입자 수 집계 (예: "2025-03")
    const monthlyData = useMemo(() => {
      const counts: { [key: string]: number } = {};
      const now = new Date();
      const last12Months = [...Array(12)].map((_, i) => {
        const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
        return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
      }).reverse(); // 최신 → 과거 순 정렬

      userStats.forEach(user => {
        const date = new Date(user.createdAt);
        const key = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
        if (last12Months.includes(key)) { // 최근 12개월 데이터만 추가
          counts[key] = (counts[key] || 0) + 1;
        }
      });

      const labels = last12Months;
      const data = labels.map(label => counts[label] || 0);

      return { labels, data };
    }, [userStats]);

  const chartData = {
    labels: monthlyData.labels,
    datasets: [
      {
        label: '가입자 수',
        data: monthlyData.data,
        backgroundColor: 'rgba(75,192,192,0.4)',
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: { position: 'top' as const },
      title: { display: true, text: '월별 회원가입 현황' },
    },
  };

  return <Bar data={chartData} options={options} />;
};

export default MonthlyRegistrationChart;
