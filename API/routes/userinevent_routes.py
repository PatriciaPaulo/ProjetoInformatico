



# Get All Events by User
@event_routes_blueprint.route('/events/<event_id>/usersinevent', methods=['GET'])
@token_required
def get_user_in_event(current_user,event_id):
    #todo order by date
    users_events = db.session.query(UserInEvent).filter(eventID=event_id).all()
    output = []
    if(current_user.status != "Organizer"):
        return make_response(jsonify({'data': [], 'message': '403 NOT OK - Forbidden'}), 403)

    for event in users_events:
        event_data = {}
        event_data['id'] = event.id
        event_data['userID'] = event.userID
        event_data['eventID'] = event.eventID
        event_data['status'] = event.status
        event_data['creator'] = event.creator

    if len(output) == 0:
        return make_response(jsonify({'data': [], 'message': '404 NOT OK - No Event Found'}), 404)

    return make_response(jsonify({'data': output, 'message': '200 OK - All Events Retrieved'}), 200)


# Add user to Event by User
@event_routes_blueprint.route('/events/<event_id>/signUpEvent', methods=['POST'])
@token_required
def add_user_to_event(current_user,event_id):
   # print("Event - "+event_id)
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)


    user_event = db.session.query(UserInEvent).filter_by(userID=current_user.id).first()
    if user_event:
        if user_event.eventID == event_id:
            return make_response(jsonify({'message': '405 NOT OK - Already signed up to Event! Update your status instead!'}), 409)

    signUp = UserInEvent(userID=current_user.id, eventID=event_id,status="Inscrito",creator = False)

    db.session.add(signUp)
    db.session.commit()

    return make_response(jsonify({'message': '200 OK - User Sign Up to Event'}), 200)

@event_routes_blueprint.route('/events/<event_id>/signUpUpdateStatusEvent/<user_event_id>', methods=['Patch'])
@token_required
def update_status_user_to_event(current_user,event_id,user_event_id):
    event = db.session.query(Event).filter_by(id=event_id).first()
    if not event:
        return make_response(jsonify({'message': '404 NOT OK - No Event Found'}), 404)

    user_event = db.session.query(UserInEvent).filter_by(id=user_event_id).first()
    if not user_event:
        return make_response(jsonify({'message': '404 NOT OK - User In Event Not Found! '}), 404)

    data = request.get_json()
    print(data)
    user_event.status = data

    db.session.commit()

    return make_response(jsonify({'message': '200 OK - User Sign Up to Event Status Updated'}), 200)
# endregion