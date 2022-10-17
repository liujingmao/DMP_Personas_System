### 步骤2： docker和docker-compose 安装

##### 2.1 docker安装 （centos7为例）

```
wget -O /etc/yum.repos.d/docker-ce.repo https://download.docker.com/linux/centos/docker-ce.repo
```

```
sudo sed -i 's+download.docker.com+mirrors.cloud.tencent.com/docker-ce+' /etc/yum.repos.d/docker-ce.repo
```

```
sudo yum makecache fast
```

```
sudo yum install -y docker-ce
```

##### 2.2 docker-compose安装

```
curl -L https://get.daocloud.io/docker/compose/releases/download/1.27.4/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
```

```
chmod +x /usr/local/bin/docker-compose
```

##### 2.3 docker 国内镜像配置

```
mkdir -p /etc/docker
```
```
vi /etc/docker/daemon.json
```

添加以下内容：

```
{
    "registry-mirrors": [" https://docker.mirrors.ustc.edu.cn"]
}
```

##### 2.4 检查 docker 是否安装成功

```
docker -v
```

##### 2.5 检查 docker-compose 是否安装成功

```
docker-compose -v
```

##### 2.6 检查 docker 是否已启动

```
systemctl daemon-reload
```
```
systemctl restart docker
```
```
systemctl status docker
```