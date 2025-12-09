import React from "react";
import {
  BellOutlined,
  AppstoreOutlined,
  DownOutlined,
} from "@ant-design/icons";
import { Dropdown} from "antd";

import UserMenuDropdown from "../UserMenu/UserMenuDropdown";
import {useLocation, useNavigate} from "react-router-dom";
import {useAppContext} from "../../layout/MainLayout";

export default function HeaderSidebar() {
  const dataUser = JSON.parse(localStorage.getItem("USER_INFO"));
  const { displayName,avatar } = dataUser;

    const location = useLocation();
    const navigate = useNavigate();

    const { countNotificationsUnRead } = useAppContext();
  return (
    <div className="flex items-center justify-between px-4 py-3 border-b">
      <Dropdown
        overlay={<UserMenuDropdown />}
        trigger={["click"]}
        placement="bottomLeft"
        overlayClassName="rounded-xl shadow-lg"
      >
        <div className="flex items-center gap-2 cursor-pointer hover:bg-gray-50 px-2 py-1 rounded-md">
         
           <img
            src={avatar || "https://i.pravatar.cc/80"}
            referrerPolicy="no-referrer"
               className="rounded-full w-6 h-6"
          />
          <span className="font-medium text-sm text-gray-700 truncate">
            {displayName}
          </span>
          <DownOutlined className="text-xs text-gray-500" />
        </div>
      </Dropdown>

        <div className="flex items-center gap-3 text-gray-500">
            <div
                onClick={() => navigate("/app/notifications")}
                className={`relative flex items-center justify-center w-7 h-7 rounded-lg cursor-pointer transition-all
                ${location.pathname === "/app/notifications"
                    ? "bg-red-50 text-red-600 font-medium"
                    : "bg-white text-gray-700 hover:bg-gray-50"}`}
            >
                <BellOutlined className="text-lg" />

                {/* Chấm thông báo */}
                {countNotificationsUnRead > 0 && (
                    <span className="absolute top-0 right-0 block w-2 h-2 rounded-full"  style={{ backgroundColor: "#F48318" }}/>
                )}
            </div>




            {/* <div
                onClick={() => navigate("")}
                className={`flex items-center justify-center w-7 h-7 rounded-lg cursor-pointer transition-all
                ${location.pathname === ""
                    ? "bg-red-50 text-red-600 font-medium"
                    : "bg-white text-gray-700 hover:bg-gray-50"}`}
            >
                <AppstoreOutlined className="text-lg" />
            </div> */}

        </div>
    </div>
  );
}
