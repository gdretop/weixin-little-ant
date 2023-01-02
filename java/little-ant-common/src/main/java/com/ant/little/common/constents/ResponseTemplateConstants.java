package com.ant.little.common.constents;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/27
 * @Version 1.0
 **/
public class ResponseTemplateConstants {
    public static String SERVER_ERROR = "服务器开小差了,等待管理员处理^_^【目前只支持 最佳路线 最短路径两种 指令,确认坐标用英文逗号隔开,没有多余换行】";
//    public static String CAN_NOT_FIND_ANSWER_SERVICE = "找不到相关指令@.@!,等待管理员查看后处理哈^_^\n(或者查看相关文档确认一下输入格式)";
    public static String CAN_NOT_FIND_ANSWER_SERVICE = "欢迎关注，点击下方菜单生存之路->指令教程可查看详细指令。或者点击右上角查看文章列表找到指令攻略。\n" +
        "\n" +
        "最短路径指令：计算到目标点最短路线。至少输入3行,2个点坐标,换行分割点,空格分割坐标。最后一行可不填，表示你的剩余步数，如下所示\n" +
        "最短路径\n" +
        "59 189\n" +
        "86 229\n" +
        "54" +
        "\n" +
        "最佳路线指令：可计算出宝箱寻找顺序，让总步数最少。输入5行,4个点坐标,换行分割点,空格分割坐标。如下所示\n" +
        "最佳路线\n" +
        "202 213\n" +
        "59 189\n" +
        "86 229\n" +
        "114 166";
}
