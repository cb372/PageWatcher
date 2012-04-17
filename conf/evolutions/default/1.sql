# Settings table

# --- !Ups

CREATE TABLE settings (
    email_smtp_host varchar(255),
    email_smtp_port int,
    email_use_tls boolean,
    email_smtp_username varchar(255),
    email_smtp_password varchar(255)
);

# --- !Downs

DROP TABLE settings;