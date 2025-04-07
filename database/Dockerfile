# Use a imagem oficial do MongoDB 8.0.1 como base
FROM mongo:8.0.1

# Definir o diretório de trabalho
WORKDIR /etc/mongo

# Copiar o arquivo de chave local para dentro da imagem
COPY mongo-keyfile /etc/mongo/mongo-keyfile

RUN chown mongodb:mongodb /etc/mongo/mongo-keyfile && \
    chmod 400 /etc/mongo/mongo-keyfile

# Copie o script de inicialização para o diretório de inicialização do MongoDB
COPY ./init-scripts/*.js /docker-entrypoint-initdb.d/

# Exponha a porta padrão do MongoDB
EXPOSE 27017
