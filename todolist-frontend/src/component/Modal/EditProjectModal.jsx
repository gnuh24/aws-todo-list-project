import { Modal, Input, message } from "antd";
import { useState, useEffect } from "react";
import { https_taskflow } from "../../service/api";

export default function EditProjectModal({
  open,
  project,
  onClose,
  onUpdated,
}) {
  const [name, setName] = useState("");

  useEffect(() => {
    if (project) setName(project.name);
  }, [project]);

  const handleSave = async () => {
    try {
      const res = await https_taskflow.patch(`/v1/projects/${project.id}`, {
        name,
        isArchived: "false",
      });
      console.log("res: ", res);

      message.success("Project updated!");
      onUpdated(project.id, name);
      onClose();
    } catch (err) {
      message.error("Failed to update");
    }
  };

  return (
    <Modal
      title="Edit Project"
      open={open}
      onCancel={onClose}
      onOk={handleSave}
      okText="Save"
    >
      <Input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Project name"
      />
    </Modal>
  );
}
