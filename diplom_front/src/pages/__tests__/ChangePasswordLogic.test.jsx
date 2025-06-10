import axiosClient from '../../api/AxiosClient';

jest.mock('../../api/AxiosClient');

const changePassword = async ({ oldPassword, newPassword, confirmPassword }, onSuccess, onError) => {
    if (newPassword !== confirmPassword) {
        onError('Пароли не совпадают!');
        return;
    }

    try {
        const response = await axiosClient.post('auth/change-password/', {
            oldPassword,
            newPassword,
        });

        if (response.status === 200) {
            onSuccess();
        } else {
            onError('Что-то пошло не так при смене пароля');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        onError('Ошибка при смене пароля');
    }
};

describe('changePassword', () => {
    const baseValues = {
        oldPassword: 'OldPass123',
        newPassword: 'NewPass456',
        confirmPassword: 'NewPass456',
    };

    it('по итогу вызов onSuccess', async () => {
        axiosClient.post.mockResolvedValueOnce({ status: 200 });

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await changePassword(baseValues, onSuccess, onError);

        expect(axiosClient.post).toHaveBeenCalledWith('auth/change-password/', {
            oldPassword: 'OldPass123',
            newPassword: 'NewPass456',
        });

        expect(onSuccess).toHaveBeenCalled();
        expect(onError).not.toHaveBeenCalled();
    });

    it('пароли не совпали', async () => {
        const onSuccess = jest.fn();
        const onError = jest.fn();

        await changePassword(
            { ...baseValues, confirmPassword: 'Mismatch123' },
            onSuccess,
            onError
        );

        expect(onSuccess).not.toHaveBeenCalled();
        expect(onError).toHaveBeenCalledWith('Пароли не совпадают!');
    });

    it('вызывает onError', async () => {
        axiosClient.post.mockRejectedValueOnce(new Error('Network error'));

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await changePassword(baseValues, onSuccess, onError);

        expect(onSuccess).not.toHaveBeenCalled();
        expect(onError).toHaveBeenCalledWith('Ошибка при смене пароля');
    });

    it('вызывает onError ', async () => {
        axiosClient.post.mockResolvedValueOnce({ status: 400 });

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await changePassword(baseValues, onSuccess, onError);

        expect(onSuccess).not.toHaveBeenCalled();
        expect(onError).toHaveBeenCalledWith('Что-то пошло не так при смене пароля');
    });
});
