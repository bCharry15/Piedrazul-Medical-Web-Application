import { inject } from '@angular/core';

import {
  CanActivateFn,
  Router,
  UrlTree,
} from '@angular/router';

import {
  Auth,
} from '../auth/auth';

const ROLE_ROUTES:
  Record<string, string> = {

  ADMIN:
    '/admin',

  SCHEDULER:
    '/scheduler',

  DOCTOR:
    '/doctor',

  PATIENT:
    '/patient',
};

function getRoleHome(
  auth: Auth,
  router: Router
): UrlTree {

  const userRoles =
    auth
      .getRoles()
      .map(
        (role) =>
          role.toUpperCase()
      );

  for (
    const [
      role,
      route,
    ] of Object.entries(
      ROLE_ROUTES
    )
  ) {

    if (
      userRoles.includes(
        role
      )
    ) {

      return router.parseUrl(
        route
      );
    }
  }

  return router.parseUrl(
    '/unauthorized'
  );
}

export const homeGuard:
  CanActivateFn = () => {

  const auth =
    inject(Auth);

  const router =
    inject(Router);

  /*
   * Sin sesión:
   * permite ver la página pública.
   */
  if (
    !auth.isAuthenticated()
  ) {
    return true;
  }

  /*
   * Con sesión:
   * dirige al portal correspondiente.
   */
  return getRoleHome(
    auth,
    router
  );
};

export function roleGuard(
  ...allowedRoles: string[]
): CanActivateFn {

  return () => {

    const auth =
      inject(Auth);

    const router =
      inject(Router);

    if (
      !auth.isAuthenticated()
    ) {

      return router.parseUrl(
        '/'
      );
    }

    const userRoles =
      auth
        .getRoles()
        .map(
          (role) =>
            role.toUpperCase()
        );

    const hasPermission =
      allowedRoles.some(
        (role) =>
          userRoles.includes(
            role.toUpperCase()
          )
      );

    if (
      hasPermission
    ) {
      return true;
    }

    return getRoleHome(
      auth,
      router
    );
  };
}