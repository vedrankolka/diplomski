# syntax=docker/dockerfile:1
# BUILD
FROM golang:1.18-buster AS build

WORKDIR /app

COPY go.mod ./
COPY go.sum ./
RUN go mod download

COPY *.go ./
RUN go build -o /localfilesink

# DEPLOY
FROM gcr.io/distroless/base-debian10
WORKDIR /
COPY --from=build /localfilesink /localfilesink
COPY ./consumer.properties /consumer.properties
USER root:root
ENTRYPOINT ["/localfilesink", "/consumer.properties"]
