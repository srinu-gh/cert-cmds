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
export class NestedFieldsInterceptor implements HttpInterceptor {
  private targetServiceUrl = 'https://api.example.com/specific-service';
  private restrictedFields: string[] = ['RUNDATE', 'modified']; // Include 'modified' here

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
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
    return next.handle(req);
  }

  private transformResponse(response: any): any {
    // Handle arrays and objects uniformly
    if (Array.isArray(response)) {
      return response.map((item) => this.traverseAndTransform(item));
    } else if (typeof response === 'object' && response !== null) {
      return this.traverseAndTransform(response);
    }
    return response;
  }

  private traverseAndTransform(obj: any): any {
    if (typeof obj !== 'object' || obj === null) {
      return obj; // Return primitive values as is
    }

    const transformedObj = { ...obj };

    for (const key in transformedObj) {
      if (transformedObj.hasOwnProperty(key)) {
        // If the key is in restricted fields, format the value
        if (this.restrictedFields.includes(key) && transformedObj[key]) {
          if (key === 'RUNDATE' && transformedObj[key].value) {
            // Format the 'value' inside RUNDATE
            transformedObj[key].value = this.formatDateWithTimezone(
              transformedObj[key].value,
              'America/New_York'
            );
          } else if (key === 'modified') {
            // Format the 'modified' field as a date
            transformedObj[key] = this.formatDateWithTimezone(
              transformedObj[key],
              'America/New_York'
            );
          }
        } else if (typeof transformedObj[key] === 'object') {
          // Recursively transform nested objects
          transformedObj[key] = this.traverseAndTransform(transformedObj[key]);
        }
      }
    }

    return transformedObj;
  }

  private formatDateWithTimezone(date: string | Date, timezone: string): string {
    if (!date) return date; // Ensure the date is not null/undefined
    return moment(date).tz(timezone).format('MM/DD/YYYY hh:mm:ss A z');
  }
}
