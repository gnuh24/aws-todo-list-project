import React, { useState } from "react";
import { Input, Button, Dropdown } from "antd";
import {
  CalendarOutlined,
  FlagOutlined,
  BellOutlined,
  MoreOutlined,
  InboxOutlined,
  ClockCircleOutlined
} from "@ant-design/icons";
import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import MoreOptionsDropdown from "../Dropdown/MoreOptionsDropdown";
import ProjectSelectDropdown from "../Dropdown/ProjectSelectDropdown";
import dayjs from "dayjs";

export default function TaskEditForm({ task, onSave, onCancel }) {
  const [taskName, setTaskName] = useState(task?.title || "");
  const [description, setDescription] = useState(task?.description || "");
  const [selectedStartTime, setSelectedStartTime] = useState(task?.startTime || null);
  const [selectedDeadline, setSelectedDeadline] = useState(task?.deadline || null);
  const [selectedProject, setSelectedProject] = useState(
      {id: task?.idProject} || {}
  );
  const [selectedSection, setSelectedSection] = useState(
      {id: task?.idSection} || {}
  );

  const [priority, setPriority] = useState(task?.priority || null);
  const formatToDisplay = "HH:mm DD/MM/YYYY";
  const formatToSend = "YYYY-MM-DDTHH:mm:ss";

  const handleSave = () => {
    if (!taskName.trim()) return;

    onSave({
      title: taskName,
      description,
      priority,
      startTime: selectedStartTime ? dayjs(selectedStartTime).format(formatToSend) : null,
      deadline: selectedDeadline ? dayjs(setSelectedDeadline).format(formatToSend) : null,
    });
  };

  return (
    <div className="border rounded-lg p-3 bg-white shadow-sm w-full">
      {/* Title input */}
      <Input
        placeholder="Edit task name..."
        value={taskName}
        onChange={(e) => setTaskName(e.target.value)}
        className="border-none text-[15px] font-medium focus:shadow-none"
      />

      {/* Description */}
      <Input.TextArea
        placeholder="Description"
        autoSize={{ minRows: 1, maxRows: 3 }}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="border-none text-[13px] text-gray-500 focus:shadow-none mt-2"
      />

      {/* Deadline hiển thị nếu khác null */}
      {selectedDeadline && (
          <div className="text-[13px] text-gray-600 mb-2 flex items-center gap-1">
            <ClockCircleOutlined className="text-orange-500" />
            <span className="text-orange-500">Deadline: {selectedDeadline ? dayjs(selectedDeadline).format(formatToDisplay) : ""}</span>
          </div>
      )}

      {/* Buttons row */}
      <div className="flex items-center gap-2 mt-3 flex-wrap">
        {/* Date picker */}
        <Dropdown
          trigger={["click"]}
          dropdownRender={() => (
            <DatePickerDropdown onSelect={(value) => setSelectedStartTime(value)} />
          )}
        >
          <Button icon={<CalendarOutlined />} size="small">
            {selectedStartTime ? dayjs(selectedStartTime).format(formatToDisplay) : "Date"}
          </Button>
        </Dropdown>

        {/* Priority */}
        <PriorityDropdown priority={priority} onSelect={setPriority} />

        {/* Reminder */}
        <Button icon={<BellOutlined />} size="small">
          Reminder
        </Button>

        {/* More options */}
        <MoreOptionsDropdown
          onSelect={(action) => console.log("Selected:", action)}
        />
      </div>

      <hr className="my-3" />

      {/* Bottom section */}
      <div className="flex justify-between items-center">
        {/* Project select */}
        <ProjectSelectDropdown
            selectedSection={selectedSection}
            selectedProject={selectedProject}
            onSelectedSection={setSelectedSection}
            onSelectedProject={setSelectedProject}
            disabled={true}
        />

        {/* Action buttons */}
        <div className="flex gap-2">
          <Button onClick={onCancel}>Cancel</Button>
          <Button
            type="primary"
            danger
            disabled={!taskName.trim()}
            onClick={handleSave}
          >
            Save
          </Button>
        </div>
      </div>
    </div>
  );
}
