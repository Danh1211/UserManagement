package com.example.usermanagement.enums;

import com.example.usermanagement.exception.IErrorMessage;
import lombok.Getter;

@Getter
public enum ErrorMessage implements IErrorMessage {
    NO_AUTHENTICATION("NO_AUTHENTICATION",
        "Bạn không có quyền thực hiện chức năng này",
        "Vui lòng kiểm tra lại!"),

    INVALID_SIGNATURE_JWT(
            "INVALID.SIGNATURE.JWT",
            "Chứ ký JWT không hợp lệ",
            "Vui lòng thử lại!"),

    JWT_EXPIRED(
            "JWT.EXPIRED",
            "JWT hết hạn",
            "Vui lòng thử lại!"),

    INVALID_JWT(
            "INVALID.JWT",
            "JWT không hợp lệ",
            "Vui lòng thử lại!"),

    REQUEST_INVALID_PARAMS(
            "REQUEST.INVALID.PARAMS",
            "Thông tin không hợp lệ",
            "Trường thông tin không được để trống"),

    WRONG_USERNAME(
            "WRONG.USERNAME",
            "Tài khoản không tồn tại",
            "Vui lòng kiểm tra lại!"),

    WRONG_PASSWORD(
            "WRONG.PASSWORD",
            "Sai mật khẩu",
            "Vui lòng kiểm tra lại!"),

    ACCOUNT_INACTIVE(
            "ACCOUNT.INACTIVE",
            "Tài khoản không hoạt động",
            "Vui lòng thử lại!"),

    ROLE_CODE_EXIST(
            "ROLE.CODE.EXIST",
            "Mã vai trò đã được sử dụng",
            "Vui lòng kiểm tra lại!"),

    PERMISSION_CODE_EXIST(
            "PERMISSION.CODE.EXIST",
            "Mã quyền đã được sử dụng",
            "Vui lòng kiểm tra lại!"),

    NOT_FOUND(
            "NOT.FOUND",
            "Không tìm thấy dữ liệu",
            "Vui lòng kiểm tra lại!"),

    CANNOT_CHANGE(
            "CANNOT.CHANGE",
            "Không thể thay đổi",
            "Vui lòng nhập lại!"),
    ;

    private final String code;
    private final String title;
    private final String message;

    ErrorMessage(String code, String title, String message) {
        this.code = code;
        this.title = title;
        this.message = message;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
