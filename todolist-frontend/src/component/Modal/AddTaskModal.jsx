import React, { useState } from "react";
import {
    Modal,
    Input,
    Button,
    Dropdown,
    Switch,
    Tag,
} from "antd";
import {
    CalendarOutlined,
    PushpinOutlined,
    UserOutlined,
    LinkOutlined,
} from "@ant-design/icons";
import dayjs from "dayjs";

import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import ProjectSelectDropdown from "../Dropdown/ProjectSelectDropdown";
import MemberDropdown from "../Dropdown/MemberDropdown";
import DateHelper from "../../helpers/DateHelper";

export default function AddTaskModal({
    open,
    onCancel,
    onAdd,
    onSelectProjectSection,
    parentTask, // ✅ NEW (optional)
    initialDate
}) {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");

    const [priority, setPriority] = useState("LOW");
    const [startTime, setStartTime] = useState(initialDate !== undefined ? initialDate : null);
    const [deadline, setDeadline] = useState(null);

    const [isPinned, setIsPinned] = useState(false);
    const [isArchived, setIsArchived] = useState(false);

    const [assigneeId, setAssigneeId] = useState(null);
    const [openMemberDropdown, setOpenMemberDropdown] = useState(false);

    const [selectedProject, setSelectedProject] = useState({
        projectId: null,
        projectName: "Inbox",
        sectionId: null,
        sectionName: null,
    });

    const resetForm = () => {
        setTitle("");
        setDescription("");
        setPriority("LOW");
        setStartTime(null);
        setDeadline(null);
        setIsPinned(false);
        setIsArchived(false);
        setAssigneeId(null);
    };

    const handleSubmit = () => {
        const newTask = {
            title: title.trim(),
            description: description || "",
            priority,
            startTime: startTime ? DateHelper.formatForServer(startTime) : null,
            deadline: deadline ? DateHelper.formatForServer(deadline) : null,
            idAccountAssign: assigneeId,
            taskFatherId: parentTask?.id || null, // ✅ IMPORTANT
            isPinned,
            isArchived,
        };
        const success = onAdd(newTask);
        if (success) resetForm();
    };

    return (
        <Modal
            open={open}
            onCancel={onCancel}
            footer={null}
            centered
            width={600}
        >
            <div className="flex flex-col gap-3">

                {/* Parent Task display */}
                {parentTask && (
                    <div className="text-xs text-gray-600 flex items-center gap-2">
                        <LinkOutlined />
                        <span className="font-medium">Parent task:</span>
                        <Tag color="blue">{parentTask.title}</Tag>
                    </div>
                )}

                {/* Title */}
                <Input
                    placeholder="Task title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="border-none text-[15px] font-medium"
                />

                {/* Description */}
                <Input.TextArea
                    placeholder="Description"
                    autoSize={{ minRows: 2, maxRows: 4 }}
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />

                {/* Time preview */}
                {(startTime || deadline) && (
                    <div className="text-[13px] text-gray-600 flex gap-4">
                        {startTime && (
                            <span>
                                ⏱ Start: {dayjs(startTime).format("DD/MM/YYYY HH:mm")}
                            </span>
                        )}
                        {deadline && (
                            <span className="text-orange-500">
                                ⏰ Deadline: {dayjs(deadline).format("DD/MM/YYYY HH:mm")}
                            </span>
                        )}
                    </div>
                )}

                {/* Actions */}
                <div className="flex items-center gap-2 flex-wrap relative">
                    <Dropdown
                        trigger={["click"]}
                        dropdownRender={() => (
                            <DatePickerDropdown onSelect={setStartTime} />
                        )}
                    >
                        <Button icon={<CalendarOutlined />}>Start time</Button>
                    </Dropdown>


                    <Dropdown
                        trigger={["click"]}
                        dropdownRender={() => (
                            <DatePickerDropdown onSelect={setDeadline} />
                        )}
                    >
                        <Button icon={<CalendarOutlined />}>Deadline</Button>
                    </Dropdown>

                    <PriorityDropdown
                        priority={priority}
                        onSelect={setPriority}
                    />

                    <Button
                        icon={<PushpinOutlined />}
                        type={isPinned ? "primary" : "default"}
                        onClick={() => setIsPinned(!isPinned)}
                    >
                        Pin
                    </Button>

                    {/* Assignee */}
                    <div className="relative">
                        <Button
                            icon={<UserOutlined />}
                            onClick={() => setOpenMemberDropdown(prev => !prev)}
                        >
                            Assign
                        </Button>

                        <MemberDropdown
                            open={openMemberDropdown}
                            taskDetail={{
                                idProject: selectedProject.projectId,
                                accountAssign: assigneeId
                                    ? { id: assigneeId }
                                    : null,
                            }}
                            onAssign={setAssigneeId}
                            onClose={() => setOpenMemberDropdown(false)}
                        />
                    </div>
                </div>

                <hr />

                {/* Bottom */}
                <div className="flex justify-between items-center">
                    {/* ✅ Hide when parentTask exists */}
                    {!parentTask && (
                        <ProjectSelectDropdown
                            selected={selectedProject}
                            onSelect={(data) => {
                                setSelectedProject(data);
                                onSelectProjectSection?.(data);
                            }}
                        />
                    )}

                    <div className="flex items-center gap-3 ml-auto">
                        <span className="text-xs">Archive</span>
                        <Switch
                            checked={isArchived}
                            onChange={setIsArchived}
                        />

                        <Button onClick={onCancel}>Cancel</Button>
                        <Button
                            type="primary"
                            danger
                            disabled={!title.trim()}
                            onClick={handleSubmit}
                        >
                            Add task
                        </Button>
                    </div>
                </div>


            </div>
        </Modal>
    );
}
