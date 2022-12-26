#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import openpyxl
from PIL import Image
from config.mori_map_config import SCALE_SIZE
import common.image_tool

"""
位置是Excel上的位置
0  位置 01  01  00B050 [  0, 176,  80] 深绿  草地岩石-fgColor.rgb
1  位置 94  26  92D050 [146, 208,  80] 浅绿  栏栅-fgColor.rgb
2  位置 07  05  F0F0F0 [  0,   0,   0] 黑色  普通地块-fgColor.rgb
3  位置 06  05  7030A0 [112,  48, 160] 紫色  山洞-fgColor.rgb  
4  位置 12  17  F196AB [241, 150, 171] 粉红  坐标牌-fgColor.rgb
5  位置 94  25  FF922B [255, 146,  43] 橘色  建筑-fgColor.theme=5
6  位置 149 152 00B0F0 [  0, 176, 240] 浅蓝  商店-fgColor.rgb
7  位置 151 148 817709 [129, 119,   9] 橙色  医院-fgColor.rgb
8  位置 140 95  FFC000 [255, 192,   0] 浅黄  教堂-fgColor.rgb
9  位置 149 148 FF0000 [255,   0,   0] 大红  加油站-fgColor.rgb
10 位置 144 124 002060 [  0,  32,  96] 深蓝  广场-fgColor.rgb
11 位置 132 94  C00000 [240, 240, 240] 深红  帐篷-fgColor.rgb 
12 位置 162 32  4290FF [ 66, 144, 255] 青蓝  工厂-fgColor.rgb 
13 位置 170 28  A219FF [162,  25, 255] 浅紫  房屋-fgColor.rgb 
"""
type_map = {
    '00B050': 0,
    '92D050': 1,
    'F0F0F0': 2,
    '7030A0': 3,
    'F196AB': 4,
    'FF922B': 5,
    '00B0F0': 6,
    '817709': 7,
    'FFC000': 8,
    'FF0000': 9,
    '002060': 10,
    'C00000': 11,
    '4290FF': 12,
    'A219FF': 13,
}
# https://www.cnblogs.com/zxt518/p/15430700.html excel 操作
dir = '/Users/yuwanglin/project/media_process/python/manual_test/地图处理/'
file_path = dir + '生存之路6.0.1.1.xlsx'
output_path = dir + '生存之路点图.png'
output_gray_path = dir + '生存之路点图gray.png'


def trans_excel_2_png():
    wb = openpyxl.load_workbook(file_path, read_only=True)
    sheet = wb['Sheet1']
    # print("Sheet2:{0}*{1}的表格".format(sheet.max_row, sheet.max_column))
    image = Image.new('RGB', (301, 301), (255, 255, 255))  # 分别是颜色模式、尺寸、颜色
    image_gray = Image.new('L', (301, 301))
    im = image.load()
    im_gray = image_gray.load()
    for row_index, row in enumerate(sheet.rows):
        for column_index, cell in enumerate(row):
            try:
                value = ''
                if cell.fill and cell.fill.fgColor:
                    if cell.fill.fgColor.theme == 5:
                        value = 'FF922B'
                    elif cell.fill.fgColor.rgb:
                        value = cell.fill.fgColor.rgb[2:8]
                    else:
                        print("问题坐标{},{}".format(row_index, column_index))
                    if value == '000000':
                        value = 'F0F0F0'
                else:
                    print("问题坐标{},{}".format(row_index, column_index))
                if value:
                    im[column_index, row_index] = common.image_tool.str_2_rgb(value)
                    im_gray[column_index, row_index] = type_map[value] * SCALE_SIZE
            except Exception as e:
                print(e)
    # image.show()  # 展示，可不要
    image.save(output_path)  # 保存路径及名称
    image_gray.save(output_gray_path)


trans_excel_2_png()
