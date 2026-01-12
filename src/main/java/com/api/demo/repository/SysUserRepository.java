package com.api.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.demo.dto.UserLoginResultProjection;
import com.api.demo.entity.SysUserEntity;
@Repository
public interface SysUserRepository extends JpaRepository<SysUserEntity, Long> {

    @Query(
        value = """
            SELECT
                U.USER_ID              AS userId,
                U.USER_NAME            AS userName,
                U.ACTIVE               AS active,
                U.USER_TYPE            AS userType,
                U.PATIENT_ID           AS patientId,
                U.STAFF_ID             AS staffId,
                U.ADMIN_FLAG           AS adminFlag,

                E.SITE_ID              AS siteId,
                E.DEPT_ID              AS deptId,
                E.EMP_NAME_ARB         AS empNameArb,
                E.EMP_NAME_ENG         AS empNameEng,
                E.EMP_TYPE             AS empType,
                E.EMP_DISPLINE         AS empDiscipline,

                S.SITE_DESC_ARB        AS siteDescArb,
                S.SITE_DESC_ENG        AS siteDescEng,

                (SELECT ID
                   FROM SYS_USER_SITE_PRVLG
                  WHERE USER_ID = U.USER_ID)
                AS sitePrivilageId

            FROM SYS_USERS U
            JOIN SYS_SITE_EMPLOYEES E ON E.ID = U.STAFF_ID
            JOIN SYS_SITES S ON S.ID = E.SITE_ID

            WHERE U.USER_NAME = :username
              AND U.USER_PASSWORD = :password
              AND U.ACTIVE = 'TRUE'
        """,
        nativeQuery = true
    )
    Optional<UserLoginResultProjection> login(
            @Param("username") String username,
            @Param("password") String password
    );


@Query(
    value = """
        SELECT
            U.USER_ID              AS userId,
            U.USER_NAME            AS userName,
            U.ACTIVE               AS active,
            U.USER_TYPE            AS userType,
            U.PATIENT_ID           AS patientId,
            U.STAFF_ID             AS staffId,
            U.ADMIN_FLAG           AS adminFlag,

            E.SITE_ID              AS siteId,
            E.DEPT_ID              AS deptId,
            E.EMP_NAME_ARB         AS empNameArb,
            E.EMP_NAME_ENG         AS empNameEng,
            E.EMP_TYPE             AS empType,
            E.EMP_DISPLINE         AS empDiscipline,

            S.SITE_DESC_ARB        AS siteDescArb,
            S.SITE_DESC_ENG        AS siteDescEng,

            (SELECT ID
               FROM SYS_USER_SITE_PRVLG
              WHERE USER_ID = U.USER_ID)
            AS sitePrivilageId

        FROM SYS_USERS U
        JOIN SYS_SITE_EMPLOYEES E ON E.ID = U.STAFF_ID
        JOIN SYS_SITES S ON S.ID = E.SITE_ID

        WHERE U.USER_NAME = :username
          AND U.ACTIVE = 'TRUE'
    """,
    nativeQuery = true
)
Optional<UserLoginResultProjection> findEmployeeByUsername(
        @Param("username") String username
);

    SysUserEntity findByUserName(String userName);

}
