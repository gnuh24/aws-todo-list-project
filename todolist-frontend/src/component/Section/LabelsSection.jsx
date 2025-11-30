import { Dropdown } from "antd";
import {useEffect, useState} from "react";
import { CloseOutlined } from "@ant-design/icons";
import {DropdownMenu} from "../Dropdown/LabelDropdown";
import {SidebarItem} from "../Modal/TaskDetailModal";
import {https_model, https_taskflow} from "../../service/api";

export function LabelsSection({taskDetail}) {
    const [selectedLabels, setSelectedLabels] = useState(taskDetail.labels);
    const [projectLabels, setProjectLabels] = useState([]);
    const [personalLabels, setPersonalLabels] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try{
                const res = await https_taskflow.get(`v1/projects/${taskDetail.idProject}/labels`)
                const listLabels = res.data.data;
                setPersonalLabels(listLabels.personalLabels)
                setProjectLabels(listLabels.projectLabels)
            }catch (error) {
                console.log(error);
            }
        }
        fetchData();
    },[])

    const handleAISuggestion = async () => {
        try{
            const res = await https_model.post("/predict",{
                title: taskDetail.title,
                description: taskDetail.description,
            })

            if (res.status === 200) {
                console.log(res.data.result.label)
                return res.data.result.label;
            }
        }catch (error) {
            console.log(error);
        }
    }

    const addLabel = async (idLabel, isPersonalLabels) => {
        try{
            if (isPersonalLabels) {
                const res = await https_taskflow.post(
                    `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}/labels`, {
                        personalLabelId: idLabel,
                    }
                );

                if (res.status === 200) {
                    const newLabel = res.data.data;
                    if (!selectedLabels.some(l => l.id === newLabel.id)) {
                        setSelectedLabels([...selectedLabels, newLabel]);
                    }
                }
            } else {
                const res = await https_taskflow.post(
                    `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}/labels`, {
                        projectLabelId: idLabel,
                    }
                );

                if (res.status === 200) {
                    const newLabel = res.data.data;
                    if (!selectedLabels.some(l => l.id === newLabel.id)) {
                        setSelectedLabels([...selectedLabels, newLabel]);
                    }
                }
            }

        }catch(err){
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                alert(msg);
            } else {
                alert("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }

    };



    const removeLabel = async (id) => {
        try{
                const res = await https_taskflow.delete(
                    `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}/labels/${id}`
                );

                if (res.status === 200) {
                    setSelectedLabels(selectedLabels.filter(l => l.id !== id));
                }

        }catch(err){
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                alert(msg);
            } else {
                alert("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    };

    const createLabel = async (name) => {
        try{
            const res = await https_taskflow.post(
                `/v1/personal-labels`,{
                    name: name,
                }
            );

            if (res.status === 200) {
                const newLabels = res.data.data;
                await addLabel(newLabels.id,true);
                setPersonalLabels((prev)=>[...prev, newLabels]);
            }
        }catch(err){
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                alert(msg);
            } else {
                alert("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    }


    return (
        <div className="flex flex-col gap-2">

            {/* Item Labels có dropdown */}
            <Dropdown
                overlay={DropdownMenu({
                    selectedLabels: selectedLabels,
                    onSelect: addLabel,
                    personalLabels,
                    sharedLabels: projectLabels,
                    onAddNew: createLabel,
                    handleAISuggestion: handleAISuggestion,
                })}
                trigger={["click"]}
            >
                <div>
                    <SidebarItem label="Labels" value="+" />
                </div>
            </Dropdown>


            {/* 📌 Danh sách các labels đã chọn (hiển thị dưới mục Labels) */}
            {selectedLabels.length > 0 && (
                <div className="ml-4 flex flex-wrap gap-2">
                    {selectedLabels.map((lb) => (
                        <div
                            key={lb.id}
                            className="flex items-center gap-1 bg-gray-100 px-2 py-1 rounded-lg w-auto"
                        >
                            <span>{lb.name}</span>

                            <CloseOutlined
                                className="cursor-pointer text-gray-500 hover:text-red-500"
                                onClick={() => removeLabel(lb.id)}
                            />
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
