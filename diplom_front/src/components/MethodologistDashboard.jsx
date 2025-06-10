import React, { useState, useEffect, useCallback } from 'react';
import axiosClient from '../api/AxiosClient';
import UsersTable from '../components/UserTable';
import AddUserWithRolesModal from '../components/AddUserModal';
import '../styles/UsersContent.css';
import ProfileViewModal from "./ProfileViewModal";

const MethodologistDashboard = () => {
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [isProfileViewModalVisible, setIsProfileViewModalVisible] = useState(false);
    const [lastnameFilter, setLastnameFilter] = useState('');
    const [roleFilter, setRoleFilter] = useState('');
    const [activeCurrentUserRole, setActiveCurrentUserRole] = useState('');
    const [users, setUsers] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalItems, setTotalItems] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const handleUserClick = (userId) => {
        setSelectedUserId(userId);
        setIsProfileViewModalVisible(true);
    };
    const [isAddUserModalVisible, setIsAddUserModalVisible] = useState(false);
    const [roles, setRoles] = useState({
        SPECIALIST: false,
        MANAGER: false,
        METHODOLOGIST: false,
        MENTOR: false,
    });
    const [activeRole, setActiveRole] = useState('');
    const [rolesBind, setRolesBind] = useState([]);
    const [formData, setFormData] = useState({
        lastName: '',
        firstName: '',
        middleName: '',
        login: ''
    });

    const fetchUsers = useCallback(async (page = 1) => {
        setIsLoading(true);
        try {
            const response = await axiosClient.get('/users/', {
                params: {
                    lastname: lastnameFilter || undefined,
                    role: roleFilter || undefined,
                    sortBy: 'lastname',
                    order: 'asc',
                    page,
                    count: 4,
                },
            });
            setUsers(response.data.items || []);
            setTotalItems(response.data.page.totalItems || 0);
        } catch (error) {
            console.error('Ошибка при загрузке пользователей:', error);
            setUsers([]);
        } finally {
            setIsLoading(false);
        }
    }, [lastnameFilter, roleFilter]);

    useEffect(() => {
        fetchUsers(currentPage);
        axiosClient.get('/user-profiles/')
            .then((response) => {
                const data = response.data;
                setActiveCurrentUserRole(data.activeRole.roleType)
            });
    }, [fetchUsers, currentPage]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleOpenAddUserModal = () => setIsAddUserModalVisible(true);

    const handleCloseAddUserModal = () => {
        setIsAddUserModalVisible(false);
        setRoles({
            SPECIALIST: false,
            MANAGER: false,
            METHODOLOGIST: false,
            MENTOR: false,
        });
        setActiveRole('');
        setRolesBind([]);
        setFormData({
            lastName: '',
            firstName: '',
            middleName: '',
            login: ''
        });
    };

    const handleRoleToggle = (roleKey) => {
        const updatedRoles = { ...roles, [roleKey]: !roles[roleKey] };
        setRoles(updatedRoles);
        const selectedRoles = Object.keys(updatedRoles).filter((key) => updatedRoles[key]);
        setRolesBind(selectedRoles);
        const active = selectedRoles[0] || '';
        setActiveRole(active);
    };

    const handleInputChange = (field, value) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
    };

    const handleSaveUser = async () => {
        const { lastName, firstName, middleName, login } = formData;
        if (!lastName || !firstName || !login) {
            alert('Пожалуйста, заполните все обязательные поля (Фамилия, Имя, Электронная почта).');
            return;
        }
        if (!activeRole) {
            alert('Пожалуйста, назначьте хотя бы одну роль.');
            return;
        }
        const userData = {
            lastName,
            firstName,
            middleName: middleName || null,
            login,
            activeStatus: true,
            roles: rolesBind,
        };
        try {
            await axiosClient.post('/users/', userData);
            handleCloseAddUserModal();
            fetchUsers(1);
        } catch (error) {
            console.error('Ошибка при сохранении пользователя:', error);
            alert('Ошибка при сохранении пользователя.');
        }
    };

    const roleLabels = {
        SPECIALIST: 'Специалист',
        MANAGER: 'Менеджер',
        METHODOLOGIST: 'Методолог',
        MENTOR: 'Ментор',
    };

    return (
        <div className="users-content">
            <h1 className="users-title">Пользователи</h1>

            <div className="filter-container">
                <div className="filter-label">Фильтр</div>
                <div className="filter-row">
                    <input
                        className="filter-input"
                        placeholder="Фамилия"
                        value={lastnameFilter}
                        onChange={(e) => setLastnameFilter(e.target.value)}
                    />
                    <select
                        className="filter-role"
                        value={roleFilter}
                        onChange={(e) => setRoleFilter(e.target.value)}
                    >
                        <option value="">Роль</option>
                        <option value="SPECIALIST">Специалист</option>
                        <option value="MENTOR">Ментор</option>
                        <option value="MANAGER">Менеджер</option>
                        <option value="METHODOLOGIST">Методолог</option>
                    </select>
                    <button className="filter-button" onClick={() => fetchUsers(1)}>Найти</button>
                </div>
            </div>


            <button className="add-user-button" onClick={handleOpenAddUserModal}>
                Добавить пользователя
            </button>

            <UsersTable
                users={users}
                loading={isLoading}
                currentPage={currentPage}
                totalItems={totalItems}
                onPageChange={handlePageChange}
                onUserClick={handleUserClick}
            />

            <AddUserWithRolesModal
                isVisible={isAddUserModalVisible}
                onClose={handleCloseAddUserModal}
                onSave={handleSaveUser}
                onInputChange={handleInputChange}
                formData={formData}
                roles={roles}
                onRoleToggle={handleRoleToggle}
            />

            <ProfileViewModal
                visible={isProfileViewModalVisible}
                onClose={() => setIsProfileViewModalVisible(false)}
                activeRole={activeCurrentUserRole}
                userId={selectedUserId}
            />
        </div>
    );
};

export default MethodologistDashboard;
