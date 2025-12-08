import React from "react";
import { Dropdown, Menu, Button } from "antd";
import {
    MoreOutlined,
    TagOutlined,
    EnvironmentOutlined,
    ClockCircleOutlined,
    ApiOutlined,
    CalendarOutlined
} from "@ant-design/icons";
import DatePickerDropdown from "./DatePickerDropdown";
import DatePickerDropdownForUpComing from "./DatePickerDropdownForUpComing";

export default function MoreOptionsDropdown({ setSelectedDateline, onSelect }) {

    const [openDatelineTab, setOpenDatelineTab] = React.useState(false);

    const menu = (
        <Menu
            className="rounded-xl shadow-lg p-2 w-44 bg-white border border-gray-200"
            items={[
                {
                    key: "labels",
                    label: (
                        <div className="flex justify-between items-center px-2 py-1 rounded-md hover:bg-gray-100 cursor-pointer">
              <span className="flex items-center gap-2">
                <TagOutlined className="text-gray-600" />
                Labels
              </span>
                            <span className="text-xs text-gray-400">@</span>
                        </div>
                    ),
                    onClick: () => onSelect?.("labels"),
                },
                {
                    key: "location",
                    label: (
                        <div className="flex items-center gap-2 px-2 py-1 rounded-md hover:bg-gray-100 cursor-pointer">
                            <EnvironmentOutlined className="text-orange-500" />
                            Location
                        </div>
                    ),
                    onClick: () => onSelect?.("location"),
                },
                {
                    key: "deadline",
                    label: (
                        <div className="flex items-center gap-2 px-2 py-1 rounded-md hover:bg-gray-100 cursor-pointer">
                            <ClockCircleOutlined className="text-orange-500" />
                            Deadline
                        </div>
                    ),
                    onClick: () => {
                        onSelect?.("deadline")
                        setOpenDatelineTab(true);
                    },
                },
                { type: "divider" },
                {
                    key: "extension",
                    label: (
                        <div className="flex items-center gap-2 px-2 py-1 rounded-md hover:bg-gray-100 cursor-pointer">
                            <ApiOutlined className="text-gray-500" />
                            Add extension...
                        </div>
                    ),
                    onClick: () => onSelect?.("extension"),
                },
                {
                    type: "divider",
                },
                {
                    key: "edit-task",
                    label: (
                        <div className="text-red-500 text-sm px-2 py-1 cursor-pointer hover:bg-red-50 rounded-md">
                            Edit task actions
                        </div>
                    ),
                    onClick: () => onSelect?.("edit-task"),
                },
            ]}
        />
    );

    return (
        <>
            <Dropdown overlay={menu} trigger={["click"]} placement="bottomRight">
                <Button icon={<MoreOutlined />} size="small" className="border-none" />
            </Dropdown>

            <Dropdown
                trigger={["click"]}
                open={openDatelineTab}
                onOpenChange={setOpenDatelineTab}
                dropdownRender={() => (
                    <DatePickerDropdownForUpComing
                        onSelect={(val) => {
                            console.log(val);
                            setSelectedDateline(val);
                            setOpenDatelineTab(false); // tắt dropdown sau khi chọn
                        }}
                        showForm={openDatelineTab}
                    />
                )}
            >
            </Dropdown>

        </>

    );
}