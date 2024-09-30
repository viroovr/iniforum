import React, { useEffect, useState } from "react";
import apiClient from "../excption/setupAxiosInterceptors";
const ProfileForm = () => {
    const [nickname, setNickname] = useState("");
    const [userName, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [profileImage, setProfileImage] = useState(null);
    const [previewImage, setPreviewImage] = useState(null);

    useEffect(() => {
        fetchProfileDetails();
    }, []);

    const fetchProfileDetails = async () => {
        try {
            const response = await apiClient.get(`/user/profile`);
            setNickname(response.data.nickname);
            setProfileImage(response.data.profileImagePath)
            setEmail(response.data.email);
            setUsername(response.data.name);
        } catch (error) {
            console.error("Error fetching question for edit: ", error);
        }
    };
    const handleSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData();
        formData.append("nickname", nickname);
        formData.append("password", password);
        formData.append("newPassword", newPassword);
        formData.append("profileImagePath", profileImage);

        try {
            const response = await apiClient.put("/user/profile", formData, {
                headers: {
                    "Content-Type": "application/json"
                },
            });

            alert("프로필이 성공적으로 업데이트 되었습니다.")
        } catch (error) {
            console.error("프로필 업데이트에 오류가 발생했습니다.", error);
        }
    }

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setProfileImage(file);

        const reader = new FileReader();
        reader.onloadend = () => {
            setPreviewImage(reader.result);
        };
        reader.readAsDataURL(file);
    };

    return (
        <div>
            <h2>프로필</h2>
            <label>이메일 : {email}</label>
            <label>이름 : {userName}</label>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>닉네임:</label>
                    <input
                        type="text"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>현재 패스워드:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>새로운 패스워드:</label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>프로필 이미지:</label>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                    />
                </div>

                {previewImage && (
                    <div>
                        <h4>이미지 미리보기</h4>
                        <img
                            src={previewImage}
                            alt="프로필 미리보기"
                            style={{width: "150px", height: "150px", objectFit: "cover"}}
                        />
                    </div>
                )}

                <button type="submit">프로필 업데이트</button>
            </form>

        </div>
    )

};

export default ProfileForm;