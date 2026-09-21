<#import "template.ftl" as layout>

<@layout.registrationLayout
    displayMessage=!messagesPerField.existsError('username', 'password')
    displayInfo=true;
    section>

    <#if section = "header">

        ${msg("loginAccountTitle")}


    <#elseif section = "form">

        <div id="kc-form">

            <div id="kc-form-wrapper">

                <#if realm.password>

                    <form
                        id="kc-form-login"
                        action="${url.loginAction}"
                        method="post"
                        onsubmit="login.disabled = true; return true;"
                    >

                        <#-- ==========================================
                             USUARIO / CORREO
                             ========================================== -->

                        <#if !usernameHidden??>

                            <div class="form-group">

                                <label
                                    for="username"
                                    class="pf-c-form__label pf-c-form__label-text"
                                >
                                    <#if !realm.loginWithEmailAllowed>
                                        ${msg("username")}
                                    <#elseif !realm.registrationEmailAsUsername>
                                        ${msg("usernameOrEmail")}
                                    <#else>
                                        ${msg("email")}
                                    </#if>
                                </label>

                                <input
                                    tabindex="1"
                                    id="username"
                                    class="pf-c-form-control"
                                    name="username"
                                    value="${(login.username!'')}"
                                    type="text"
                                    autofocus
                                    autocomplete="username"
                                    aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                                />

                                <#if messagesPerField.existsError('username','password')>

                                    <span
                                        id="input-error"
                                        class="pf-c-form__helper-text pf-m-error required kc-feedback-text"
                                        aria-live="polite"
                                    >
                                        ${kcSanitize(
                                            messagesPerField.getFirstError(
                                                'username',
                                                'password'
                                            )
                                        )?no_esc}
                                    </span>

                                </#if>

                            </div>

                        </#if>


                        <#-- ==========================================
                             CONTRASEÑA
                             ========================================== -->

                        <div class="form-group">

                            <label
                                for="password"
                                class="pf-c-form__label pf-c-form__label-text"
                            >
                                ${msg("password")}
                            </label>


                            <div class="pf-c-input-group">

                                <input
                                    tabindex="2"
                                    id="password"
                                    class="pf-c-form-control"
                                    name="password"
                                    type="password"
                                    autocomplete="current-password"
                                    aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                                />

                                <button
                                    class="pf-c-button pf-m-control"
                                    type="button"
                                    aria-label="${msg('showPassword')}"
                                    aria-controls="password"
                                    data-password-toggle
                                >

                                    <span
                                        class="fa fa-eye"
                                        aria-hidden="true"
                                    ></span>

                                </button>

                            </div>

                        </div>


                        <#-- ==========================================
                             RECORDARME + RECUPERAR CONTRASEÑA
                             ========================================== -->

                        <div
                            id="kc-form-options"
                            class="form-options"
                        >

                            <#if realm.rememberMe && !usernameHidden??>

                                <div class="checkbox">

                                    <label>

                                        <#if login.rememberMe??>

                                            <input
                                                tabindex="3"
                                                id="rememberMe"
                                                name="rememberMe"
                                                type="checkbox"
                                                checked
                                            />

                                        <#else>

                                            <input
                                                tabindex="3"
                                                id="rememberMe"
                                                name="rememberMe"
                                                type="checkbox"
                                            />

                                        </#if>

                                        ${msg("rememberMe")}

                                    </label>

                                </div>

                            </#if>


                            <#if realm.resetPasswordAllowed>

                                <div class="forgot-password">

                                    <a
                                        tabindex="4"
                                        href="${url.loginResetCredentialsUrl}"
                                    >
                                        ${msg("doForgotPassword")}
                                    </a>

                                </div>

                            </#if>

                        </div>


                        <#-- ==========================================
                             CREDENCIAL SELECCIONADA
                             ========================================== -->

                        <input
                            type="hidden"
                            id="id-hidden-input"
                            name="credentialId"
                            <#if auth.selectedCredential?has_content>
                                value="${auth.selectedCredential}"
                            </#if>
                        />


                        <#-- ==========================================
                             BOTÓN LOGIN
                             ========================================== -->

                        <div
                            id="kc-form-buttons"
                            class="form-group"
                        >

                            <input
                                tabindex="5"
                                class="pf-c-button pf-m-primary pf-m-block btn-lg"
                                name="login"
                                id="kc-login"
                                type="submit"
                                value="${msg('doLogIn')}"
                            />

                        </div>

                    </form>


                    <#-- ==========================================
                         SCRIPT MOSTRAR / OCULTAR CONTRASEÑA
                         ========================================== -->

                    <script>
                        (function () {

                            const passwordInput =
                                document.getElementById(
                                    'password'
                                );

                            const toggleButton =
                                document.querySelector(
                                    '[data-password-toggle]'
                                );

                            if (
                                !passwordInput ||
                                !toggleButton
                            ) {
                                return;
                            }

                            toggleButton.addEventListener(
                                'click',
                                function () {

                                    const isPassword =
                                        passwordInput.type ===
                                        'password';

                                    passwordInput.type =
                                        isPassword
                                            ? 'text'
                                            : 'password';

                                    const icon =
                                        toggleButton
                                            .querySelector(
                                                'span'
                                            );

                                    if (icon) {

                                        icon.classList.toggle(
                                            'fa-eye',
                                            !isPassword
                                        );

                                        icon.classList.toggle(
                                            'fa-eye-slash',
                                            isPassword
                                        );
                                    }
                                }
                            );

                        })();
                    </script>

                </#if>

            </div>

        </div>


    <#elseif section = "info">

        <div
            id="kc-registration"
            class="piedraazul-registration"
        >

            <span>
                ${msg("noAccount")}
            </span>

            <a
                href="http://localhost:4200/register"
                id="piedraazul-create-account"
            >
                ${msg("createAccount")}
            </a>

        </div>

    </#if>

</@layout.registrationLayout>