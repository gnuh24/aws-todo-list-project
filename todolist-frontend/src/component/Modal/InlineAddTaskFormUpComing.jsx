import React, { useState } from "react";
import dayjs from "dayjs";
import { Input, Button, Dropdown } from "antd";
import { CalendarOutlined, BellOutlined, ClockCircleOutlined } from "@ant-design/icons";
import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import MoreOptionsDropdown from "../Dropdown/MoreOptionsDropdown";
import ProjectSelectDropdown from "../Dropdown/ProjectSelectDropdown";
import {https_taskflow} from "../../service/api";
import {toast} from "sonner";

export default function InlineAddTaskFormUpComing({ initialDate, onCancel, onAdd }) {
  const [taskName, setTaskName] = useState("");
  const [description, setDescription] = useState("");
  const selectedStartTime = dayjs(initialDate);
  const [selectedSection, setSelectedSection] = useState({});
  const [selectedProject, setSelectedProject] = useState({});
  const [selectedDeadline, setSelectedDeadline] = useState(null);
  const [priority, setPriority] = useState("LOW");
  const formatToDisplay = "HH:mm DD/MM/YYYY";
  const formatToSend = "YYYY-MM-DDTHH:mm:ss";
  const [showFormDatePicker, setShowFormDatePicker] = useState(false);

    const resetForm = async () => {
        setTaskName("");
        setDescription("");
        setSelectedSection({});
        setSelectedDeadline(null);
        setSelectedProject({});
        setPriority("LOW");
    }

    const handleAddTask = async () => {
        if (!taskName.trim()) return;

        if (selectedSection.id === undefined) {
            toast.info("Vui lòng chọn project / section");
            return;
        }

        const taskDTO = {
            title: taskName,
            description,
            startTime: selectedStartTime.format(formatToSend),
            deadline: selectedDeadline ? selectedDeadline.format(formatToSend) : null,
            sectionId: selectedSection.id,
            priority: priority,
        };

        try {
            const response = await https_taskflow.post(
                `/v1/projects/${selectedProject.id}/tasks`, // dùng biến idProject trong hàm
                taskDTO,
            );
            if (response.status === 200) {
                onAdd(response.data.data);
                resetForm();
                toast.success("Thêm thành công")
            }
        } catch (err) {
            console.error("Add task failed", err);

            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                toast.error(msg);
            } else {
                toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    };



    return (
    <div className="border rounded-lg p-4 mt-2 w-full bg-white shadow-sm">
      {/* Task input */}
      <Input
        placeholder="Meet with tutor Friday at 3pm"
        value={taskName}
        onChange={(e) => setTaskName(e.target.value)}
        className="border-none text-[14px] font-medium focus:shadow-none"
      />

      {/* Description */}
      <Input.TextArea
        placeholder="Description"
        autoSize={{ minRows: 1, maxRows: 3 }}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="border-none text-[13px] text-gray-500 mt-1 mb-3 focus:shadow-none"
      />

        {/* Deadline hiển thị nếu khác null */}
        {selectedDeadline && (
            <div className="text-[13px] text-gray-600 mb-2 flex items-center gap-1">
                <ClockCircleOutlined className="text-orange-500" />
                <span className="text-orange-500">Deadline: {selectedDeadline ? selectedDeadline.format(formatToDisplay) : ""}</span>
            </div>
        )}

      {/* Action buttons row */}
      <div className="flex flex-wrap gap-2 mb-3">
          <Button icon={<CalendarOutlined />} size="small">
                  {selectedStartTime.format(formatToDisplay)}
          </Button>


        <PriorityDropdown priority={priority} onSelect={setPriority} />
        <Button icon={<BellOutlined />} size="small">
          Reminders
        </Button>
        <MoreOptionsDropdown
            setSelectedDateline={setSelectedDeadline}
        />
      </div>

      <hr />

      {/* Footer actions */}
      <div className="flex justify-between items-center mt-3">
        <ProjectSelectDropdown
          selectedSection={selectedSection}
          selectedProject={selectedProject}
          onSelectedSection={setSelectedSection}
          onSelectedProject={setSelectedProject}
        />

        <div className="flex gap-2">
          <Button onClick={onCancel}>
            Cancel
          </Button>
          <Button
            type="primary"
            danger
            disabled={!taskName.trim()}
            onClick={() => {
              handleAddTask();
            }}
          >
            Add task
          </Button>
        </div>
      </div>
    </div>
  );
}
