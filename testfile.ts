import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment-timezone';

@Injectable()
export class DateFormatResponseInterceptor implements HttpInterceptor {
  // URL of the target service
  private targetServiceUrl = 'https://api.example.com/specific-service';

  // Fields to transform (including nested ones)
  private restrictedFields: string[] = ['runDate', 'startDate', 'endDate'];

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Check if the request URL matches the target service
    if (req.url.includes(this.targetServiceUrl)) {
      return next.handle(req).pipe(
        map((event) => {
          if (event instanceof HttpResponse && event.body) {
            const body = this.transformResponse(event.body);
            return event.clone({ body });
          }
          return event;
        })
      );
    }

    // If not the target service, pass the request unmodified
    return next.handle(req);
  }

  private transformResponse(response: any): any {
    if (Array.isArray(response)) {
      return response.map((item) => this.traverseAndTransform(item));
    } else if (typeof response === 'object') {
      return this.traverseAndTransform(response);
    }
    return response;
  }

  private traverseAndTransform(obj: any): any {
    if (typeof obj !== 'object' || obj === null) {
      return obj;
    }

    const transformedObj = { ...obj };

    for (const key in transformedObj) {
      if (transformedObj.hasOwnProperty(key)) {
        if (this.restrictedFields.includes(key) && transformedObj[key]) {
          // Transform the field if it matches the restrictedFields
          transformedObj[key] = this.formatDateWithTimezone(
            transformedObj[key],
            'America/New_York'
          );
        } else if (typeof transformedObj[key] === 'object') {
          // Recursively transform child objects
          transformedObj[key] = this.traverseAndTransform(transformedObj[key]);
        }
      }
    }

    return transformedObj;
  }

  private formatDateWithTimezone(date: string | Date, timezone: string): string {
    return moment(date).tz(timezone).format('MM/DD/YYYY hh:mm:ss A z');
  }
}
