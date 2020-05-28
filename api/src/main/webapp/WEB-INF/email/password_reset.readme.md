# Reset Password<br/><sup>Email Template</sup>

>   **Template File Name:** password_reset.html
>
>   **Language:** Thai
>
>   **Engine:** Apache Commons Text (org.apache.commons.text.StringSubstitutor)
>   **Alternate Engine:** not required



## Required Variables

>   Using Map<String name, String value> **valuesMap** with required name below.

| name         | sample value    | description                                    |
| ------------ | --------------- | ---------------------------------------------- |
| ${USER_NAME} | ประสิทธิ์ จิตมโนโสต | Full name or login name used to call receiver. |
| ${PASSWORD}  | 12345678        | Readable word without encryption.              |



## Limit of the use

| Limit                                                        | Remark                               |
| ------------------------------------------------------------ | ------------------------------------ |
| Can't put the customizable data table on the content of this email. | Perfected for notify mail like this. |





----

-- end of document --