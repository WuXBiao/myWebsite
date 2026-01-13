# 简历文件说明

简历文件不存放在代码仓库中，而是直接上传到服务器指定目录。

## 服务器目录
```
/data/resume/resume.pdf
```

## 部署步骤

1. 在服务器上创建目录：
```bash
sudo mkdir -p /data/resume
sudo chmod 755 /data/resume
```

2. 上传简历文件到服务器：
```bash
scp your-resume.pdf user@your-server:/data/resume/resume.pdf
```

## 访问方式
- 网页预览：`http://你的网站地址/resume`
- 直接下载：`http://你的网站地址/resume/resume.pdf`

## 注意事项
1. 确保 PDF 文件名为 `resume.pdf`
2. 文件大小建议不超过 10MB
3. PDF 内容应包含简历的所有信息
4. 建议使用 A4 页面格式
5. 服务器目录 `/data/resume` 会以只读方式挂载到容器中
