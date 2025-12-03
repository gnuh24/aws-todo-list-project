import React from "react";
import { useNavigate } from "react-router-dom";

export default function SidebarItem({
  icon,
  label,
  count,
  active,
  onClick,
  className = "",
  countClass = "",
}) {
  const isAddTask = label.toLowerCase() === "add task";

  return (
    <div
      onClick={onClick}
      className={`flex justify-between items-center px-3 py-2 cursor-pointer rounded-md 
        transition-colors duration-150 select-none
        ${
          isAddTask
            ? "text-red-500 hover:bg-red-50 hover:text-red-600 font-medium"
            : active
            ? "bg-red-50 text-red-600 font-medium"
            : "text-gray-700 hover:bg-gray-50"
        } ${className}`}
    >
      <div className="flex items-center gap-2">
        {icon}
        <span className="truncate text-[13px]">{label}</span>
      </div>
      {count && <span className={`text-xs ${countClass}`}>{count}</span>}
    </div>
  );
}
