package com.lm.order.Eumn;

/**
 * 订单状态枚举
 */
public enum OrderStatus {

    // 订单创建阶段
    PRE_CREATE(0, "预创建", "订单信息已接收，待处理"),
    CREATING(1, "创建中", "订单正在处理中"),
    WAITING_PAY(2, "待支付", "订单已创建，等待用户支付"),
    CREATE_FAILED(3, "创建失败", "订单创建失败"),

    // 支付阶段
    PAYING(4, "支付中", "用户正在支付"),
    PAID(5, "已支付", "支付成功，待发货"),
    PAY_FAILED(6, "支付失败", "支付失败"),
    PAY_TIMEOUT(7, "支付超时", "未在规定时间内支付"),

    // 履约阶段
    WAITING_SHIP(8, "待发货", "已支付，等待商家发货"),
    PART_SHIPPED(9, "部分发货", "部分商品已发货"),
    SHIPPED(10, "已发货", "商品已发货，运输中"),
    DELIVERING(11, "配送中", "商品正在配送中"),

    // 完成阶段
    COMPLETED(12, "已完成", "订单已完成，已收货"),
    CONFIRMED(13, "已确认收货", "用户已确认收货"),

    // 售后阶段
    APPLY_REFUND(14, "申请退款", "用户申请退款"),
    REFUNDING(15, "退款中", "退款处理中"),
    REFUNDED(16, "已退款", "退款成功"),
    REFUND_FAILED(17, "退款失败", "退款失败"),

    // 取消关闭阶段
    CANCELING(18, "取消中", "订单取消处理中"),
    CANCELED(19, "已取消", "订单已取消"),
    CLOSED(20, "已关闭", "订单已关闭"),

    // 异常状态
    EXCEPTION(21, "异常订单", "订单状态异常，需人工处理");

    private final int code;
    private final String status;
    private final String description;

    OrderStatus(int code, String status, String description) {
        this.code = code;
        this.status = status;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static OrderStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为终态（不可再变更的状态）
     */
    public boolean isFinalStatus() {
        return this == COMPLETED ||
                this == CONFIRMED ||
                this == CANCELED ||
                this == CLOSED ||
                this == REFUNDED ||
                this == CREATE_FAILED;
    }

    /**
     * 判断是否可以取消
     */
    public boolean canCancel() {
        return this == PRE_CREATE ||
                this == CREATING ||
                this == WAITING_PAY ||
                this == WAITING_SHIP;
    }

    /**
     * 判断是否可以支付
     */
    public boolean canPay() {
        return this == WAITING_PAY;
    }

    /**
     * 判断是否可以发货
     */
    public boolean canShip() {
        return this == WAITING_SHIP || this == PAID;
    }

    /**
     * 获取状态流转提示信息
     */
    public String getFlowTip() {
        switch (this) {
            case WAITING_PAY:
                return "请在30分钟内完成支付";
            case WAITING_SHIP:
                return "商家将在48小时内发货";
            case SHIPPED:
                return "商品已发出，请耐心等待";
            case COMPLETED:
                return "订单已完成，感谢您的购买";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return status + "(" + code + ") - " + description;
    }
}