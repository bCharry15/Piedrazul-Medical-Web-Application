import { Injectable } from '@angular/core';

import Keycloak, {
  KeycloakProfile,
} from 'keycloak-js';

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private readonly keycloak =
    new Keycloak({
      url: 'http://localhost:8085',
      realm: 'PiedrAzul',
      clientId: 'piedraazul-spa',
    });

  async init(): Promise<boolean> {
    return this.keycloak.init({
      onLoad: 'check-sso',
      pkceMethod: 'S256',
      checkLoginIframe: false,
    });
  }

  login(): Promise<void> {
    return this.keycloak.login({
      redirectUri:
        window.location.origin,
    });
  }

  logout(): Promise<void> {
    return this.keycloak.logout({
      redirectUri:
        window.location.origin,
    });
  }

  register(): Promise<void> {
    return this.keycloak.register({
      redirectUri:
        window.location.origin,
    });
  }

  isAuthenticated(): boolean {
    return Boolean(
      this.keycloak.authenticated
    );
  }

  getToken():
    string | undefined {

    return this.keycloak.token;
  }

  async getValidToken():
    Promise<string | undefined> {

    if (
      !this.keycloak.authenticated
    ) {
      return undefined;
    }

    try {

      await this.keycloak.updateToken(
        30
      );

      return this.keycloak.token;

    } catch (error) {

      console.error(
        'Could not refresh Keycloak token:',
        error
      );

      return this.keycloak.token;
    }
  }

  getUsername(): string {
    return (
      this.keycloak
        .tokenParsed?.[
          'preferred_username'
        ] ?? ''
    );
  }

  getRoles(): string[] {

    const realmAccess =
      this.keycloak
        .tokenParsed?.[
          'realm_access'
        ];

    return (
      realmAccess?.roles ?? []
    );
  }

  hasRole(
    role: string
  ): boolean {

    return this
      .getRoles()
      .includes(role);
  }

  async loadUserProfile():
    Promise<KeycloakProfile> {

    return this.keycloak
      .loadUserProfile();
  }

  async refreshToken():
    Promise<boolean> {

    return this.keycloak
      .updateToken(30);
  }
}