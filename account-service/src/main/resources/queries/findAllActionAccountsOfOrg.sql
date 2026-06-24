select distinct
acct.id_cntryprty_acct,
acct.name_cntprty_acct,
acct.id_orgtn,
CONCAT(
    ' | ',
    CONCAT(
        COALESCE(ADDR.NAME_CITY, ''),
        CONCAT(
            ' | ',
            COALESCE(ADDR.CD_ST, '')
        )
    )
) AS SEARCH_KEY
from ucs_cntprty_acct_role_assn assn
join ucs_cntprty_acct acct on acct.id_cntryprty_acct = assn.id_cntryprty_acct
left join ucs_address ADDR on ADDR.id_cntryprty_acct = acct.id_cntryprty_acct
where acct.id_orgtn = :orgId
