// frontend/app/components/MonthlyRegistrationChart.tsx
"use client";

import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface MonthlyRegistrationChartProps {
  userStats: { createdAt: string }[];
}

export default function MonthlyRegistrationChart({ userStats }: MonthlyRegistrationChartProps) {
  const now = new Date();
  const last12Months = [...Array(12)].map((_, i) => {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
    return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
  }).reverse();

  const counts: Record<string, number> = {};
  last12Months.forEach(month => {
    counts[month] = 0;
  });
  userStats.forEach(user => {
    const date = new Date(user.createdAt);
    const key = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
    if (counts[key] !== undefined) {
      counts[key] += 1;
    }
  });

  const data = {
    labels: last12Months,
    datasets: [
      {
        label: "가입자 수",
        data: last12Months.map(month => counts[month]),
        backgroundColor: "rgba(75, 192, 192, 0.6)",
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: true, position: "top" as const },
      title: { display: true, text: "월별 회원가입 현황", font: { size: 16 } },
    },
    scales: {
      x: { ticks: { font: { size: 12 } } },
      y: { ticks: { font: { size: 12 } }, beginAtZero: true },
    },
  };

  return (
    <div style={{ height: "300px", width: "100%" }}>
      <Bar data={data} options={options} />
    </div>
  );
}
