FROM quay.io/quarkus/quarkus-distroless-image:2.0
COPY target/*-runner /application

RUN chmod +x /application

EXPOSE 8080
USER nonroot

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]