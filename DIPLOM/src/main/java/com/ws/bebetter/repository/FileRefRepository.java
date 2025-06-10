package com.ws.bebetter.repository;

import com.ws.bebetter.entity.FileRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRefRepository extends JpaRepository<FileRef, UUID> {

}
