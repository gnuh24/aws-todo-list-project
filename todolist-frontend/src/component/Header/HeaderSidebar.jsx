import React from "react";
import {
  BellOutlined,
  AppstoreOutlined,
  DownOutlined,
} from "@ant-design/icons";
import { Button, Dropdown, Menu } from "antd";

import UserMenuDropdown from "../UserMenu/UserMenuDropdown";

export default function HeaderSidebar() {
  const dataUser = JSON.parse(localStorage.getItem("USER_INFO"));
  const { displayName } = dataUser;
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
            src="https://i.pravatar.cc/30"
            alt="avatar"
            className="rounded-full w-6 h-6"
          />
          <span className="font-medium text-sm text-gray-700 truncate">
            {displayName}
          </span>
          <DownOutlined className="text-xs text-gray-500" />
        </div>
      </Dropdown>

      <div className="flex items-center gap-3 text-gray-500">
        <BellOutlined className="text-base hover:text-black cursor-pointer" />
        <AppstoreOutlined className="text-base hover:text-black cursor-pointer" />
      </div>
    </div>
  );
}
